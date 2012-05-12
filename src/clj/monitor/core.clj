(ns clj.monitor.core
  (:use [control.core]
        [clj.monitor.timer]
        [clojure.tools.logging :only (info warn error debug infof debugf)]
        [clojure.walk :only [walk]]
        [clj.monitor.alerts]))

(defn ^:private check-valid-options
  "Throws an exception if the given option map contains keys not listed
  as valid, else returns nil."
  [options & valid-keys]
  (when (seq (apply disj (apply hash-set (keys options)) valid-keys))
    (throw
     (IllegalArgumentException.
      (apply str "Only these options are valid: "
             (first valid-keys)
             (map #(str ", " %) (rest valid-keys)))))))

;;All defined monitors
(defonce ^:private *monitors (atom {}))

(defn install-monitor
  "Install a monitor with name."
  [name monitor]
  (check-valid-options monitor :name :tasks :clusters :host)
  (swap! *monitors assoc (keyword name) monitor))

(defn get-monitor [name]
  "Get a monitor by name"
  (if (map? name)
    name
    (get @*monitors name)))

(defn clear-monitors
  "Clear all defined monitors"
  []
  (reset! *monitors {}))

(defonce ^:private retry-times (atom 0))

(defn reset-retry-times-
  "Reset retry times to zero"
  []
  (reset! retry-times 0))

(defn inc-retry-times-
  []
  (swap! retry-times inc))

(defn- unquote-opts [args]
  (walk (fn [item]
          (cond (and (seq? item) (= `unquote (first item)))
                (second item)
                (or (seq? item) (symbol? item))
                (list 'quote item)
                :else
                (unquote-opts item)))
        identity
        args))

(defmacro defmonitor
  "Define a monitor with options, valid options including:

          :tasks       a vector of tasks for monitoring
          :clusters   a vector of clusters for monitoring,the clusters must have been defined by defcluster.
          :host        a string in the form of \"user@host\",if  this is provided, clj.monitor will use this user@host for monitoring instead of clusters.

  An example:
        (defmonitor mysql-monitor
               :tasks [ (ping-msyql \"user\" \"password\")]
               :host \"root@mysql.app.com\")
  "
  [mname & opts]
  (let [mname (keyword mname)]
    `(let [m# (apply hash-map ~(cons 'list (unquote-opts opts)))]
       ;;(check-valid-options m# :tasks :clusters :host :cron :error :every)
       (install-monitor ~mname (assoc m# :name ~mname)))))

(defn- exec-tasks
  "Execute tasks with clusters or an \"user@host\" string"
  [tasks coh enable-logging]
  (debugf "Execute tasks %s on cluster-or-host %s" tasks coh)
  (try
    (binding [*enable-logging* enable-logging]
      (reduce (fn [rt task]
                (let [tname (str task)
                      value (or (get rt tname) {})
                      result (if (coll? coh)
                               (mapcat #(do-begin (list* (name %) task)) coh)
                               (do-begin (list* (if (keyword? coh) (name coh) coh) task)))]
                  (assoc rt tname (merge value  result)))) {}  tasks))
    (catch Throwable t
      (error t "Execute tasks failed")
      {:exception t})))

(defn- alert
  "Send alert message to alert functions"
  [rt alerts]
  (try
    (doseq [alert-form alerts]
      (if-let [f (get-alert-fn (first alert-form))]
        (apply f (cons rt (next alert-form)))
        (throw (RuntimeException. (format "Could not find alert fn :  %s" alert-form)))))
    (finally
     ;;remember to reset retry times to be zero
     (reset-retry-times-))))

(defn- pick-error-monitors
  "Pick all monitors that have exception or monitor failed."
  [rt]
  (into {} (filter (fn [[monitor m]]
                     ;;Contains exception
                     (or (contains? m :exception)
                         ;;or any task failed
                         (some  (fn [[task hm]]
                                  (not-every? true? (vals hm)))  m)))  rt)))

(defonce *monitor-started (atom false))

(defmacro start-monitors
  "Start monitors with options,if it has been started,this will throw an exception.
   Valid options including:

             :monitors    a vector of monitors in keyword,the monitors must have been defined by defmonitor.
             :alerts      a vector of alert functions.
             :parallel   whether to execute monitor task in parallel between monitors.
             :quartz-threads    Quratz thread number,default is CPUs.
             :cron        a crontab-like string to set monitors running time.
             :max-retry-times    max retry times to monitor when found error.
                                            When finding monito error,we will try it again at once.If the retry times is over this value,then we send alerts.

    An example:

        (start-monitor
              :monitors [:mysql-monitor]
              :alerts [ (mail :from \"alert@app.com\" to: \"me@app.com\")]
              :cron   \"* */10 * * * ?\")
   "
  [ & opts]
  (when @*monitor-started
    (throw (RuntimeException. "clj.monitors has been started")))
  (let [m (apply hash-map opts)
        alerts (:alerts m)
        parallel (:parallel m)
        max-retry-times (or (:max-retry-times m) 3)
        enable-control-log (and (:enable-control-logging m) true)
        map-fn (if parallel pmap map)
        quartz-threads (or (:quartz-threads m) (.. (Runtime/getRuntime) (availableProcessors)))
        sc (init-scheduler quartz-threads)
        cron (or (:cron m) "* 0/5 * * * ?")
        monitors (map #(get-monitor (keyword %)) (:monitors m))]
    (infof "Schedule monitor task:%s" cron)
    (letfn [(sc-fn  []
              (try
                (let [rt (apply hash-map (apply concat
                                                (map-fn (fn [mt]
                                                          (let [tasks (:tasks mt)
                                                                clusters-or-host (or (:host mt) (map keyword (:clusters mt)))
                                                                hr (exec-tasks tasks clusters-or-host enable-control-log)]
                                                            [(:name mt) (into {} hr)])) monitors)))
                      error-monitors (pick-error-monitors rt)
                      has-error (not (empty? error-monitors))
                      over-max (>= @retry-times max-retry-times)]
                  (cond
                   (and has-error over-max) (alert error-monitors alerts)
                   has-error (do (inc-retry-times-) (sc-fn))
                   over-max (reset-retry-times-)))
                (catch Throwable t
                  (error t "Monitor failed")
                  (if (>= @retry-times max-retry-times)
                    (alert t alerts)
                    (do (inc-retry-times-) (sc-fn))))))]
      (schedule-task sc sc-fn  cron))
    (info "Start monitor schduler...")
    (start-scheduler sc)
    (reset! *monitor-started sc)))

(defn stop-monitors
  "Stop monitors"
  []
  (let [sc @*monitor-started]
    (stop-scheduler sc)
    (reset! *monitor-started false)))

;;load pre-defined tasks and alert functions
(load "/clj/monitor/tasks")
