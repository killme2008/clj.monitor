(ns clj.monitor.alerts
  (:require [clojure.pprint :as pp] )
  (:use [clojure.tools.logging :only (info warn)]
        [postal.core]))

(defonce ^:private *alert-fns (atom {}))

(defn get-alert-fn
  "Get alert fn by name"
  [name]
  (if (fn? name)
    name
    (get @*alert-fns (keyword name))))

(defn install-alert-fn!
  "Install a alert fn with name"
  [name f & kvs]
  (swap! *alert-fns assoc (keyword name) f)
  (when kvs
    (recur (first kvs) (second kvs) (next kvs))))

(defn pprint-str
  ([obj] (pprint-str "[Alert]:" obj))
  ([prefix obj]
     (with-out-str
       (println prefix)
       (pp/pprint obj))))

(defn mail-alert
  [rt & opts]
  (let [m (apply hash-map opts)]
    (send-message (merge {:body (pprint-str rt) :subject "Alert from clj.monitor"} m))))

(defn console-alert [rt & opts]
  (println (pprint-str rt)))

(defn log-alert [rt  & opts]
  (warn (pprint-str rt)))

(install-alert-fn! :log log-alert :mail mail-alert :console console-alert)
