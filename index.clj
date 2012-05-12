{:namespaces
 ({:source-url nil,
   :wiki-url "clj.monitor.alerts-api.html",
   :name "clj.monitor.alerts",
   :doc nil}
  {:source-url nil,
   :wiki-url "clj.monitor.core-api.html",
   :name "clj.monitor.core",
   :doc nil}
  {:source-url nil,
   :wiki-url "clj.monitor.tasks-api.html",
   :name "clj.monitor.tasks",
   :doc nil}
  {:source-url nil,
   :wiki-url "clj.monitor.timer-api.html",
   :name "clj.monitor.timer",
   :doc nil}),
 :vars
 ({:arglists ([]),
   :name "clear-alert-fns",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/clear-alert-fns",
   :doc "Clear all installed alert functions",
   :var-type "function",
   :line 15,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([rt & opts]),
   :name "console-alert",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/console-alert",
   :doc "Print alert messages to console",
   :var-type "function",
   :line 45,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([name]),
   :name "get-alert-fn",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/get-alert-fn",
   :doc "Get alert fn by name",
   :var-type "function",
   :line 8,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([name f & kvs]),
   :name "install-alert-fn",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/install-alert-fn",
   :doc "Install a alert fn with name",
   :var-type "function",
   :line 20,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([rt & opts]),
   :name "log-alert",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/log-alert",
   :doc "Print alert message using tools.logging warn",
   :var-type "function",
   :line 50,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([rt & opts]),
   :name "mail-alert",
   :namespace "clj.monitor.alerts",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.alerts-api.html#clj.monitor.alerts/mail-alert",
   :doc
   "Send alert messages to a email address,valid options including:\n   :from    sender address\n   :to        receiver address,can be a vector\n   :subject   email subject,default is \"Alert from clj.monitor\".\n   :nody       email body.\n",
   :var-type "function",
   :line 34,
   :file "src/clj/monitor/alerts.clj"}
  {:arglists ([]),
   :name "clear-monitors",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.core-api.html#clj.monitor.core/clear-monitors",
   :doc "Clear all defined monitors",
   :var-type "function",
   :line 34,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([mname & opts]),
   :name "defmonitor",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url "/clj.monitor.core-api.html#clj.monitor.core/defmonitor",
   :doc
   "Define a monitor with options, valid options including:\n\n        :tasks       a vector of tasks for monitoring\n        :clusters   a vector of clusters for monitoring,the clusters must have been defined by defcluster.\n        :host        a string in the form of \"user@host\",if  this is provided, clj.monitor will use this user@host for monitoring instead of clusters.\n\nAn example:\n      (defmonitor mysql-monitor\n             :tasks [ (ping-msyql \"user\" \"password\")]\n             :host \"root@mysql.app.com\")\n",
   :var-type "macro",
   :line 61,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([name monitor]),
   :name "install-monitor",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.core-api.html#clj.monitor.core/install-monitor",
   :doc "Install a monitor with name.",
   :var-type "function",
   :line 22,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([]),
   :name "reset-retry-times-",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.core-api.html#clj.monitor.core/reset-retry-times-",
   :doc "Reset retry times to zero",
   :var-type "function",
   :line 41,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([& opts]),
   :name "start-monitors",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.core-api.html#clj.monitor.core/start-monitors",
   :doc
   "Start monitors with options,if it has been started,this will throw an exception.\nValid options including:\n\n          :monitors    a vector of monitors in keyword,the monitors must have been defined by defmonitor.\n          :alerts      a vector of alert functions.\n          :parallel   whether to execute monitor task in parallel between monitors.\n          :quartz-threads    Quratz thread number,default is CPUs.\n          :cron        a crontab-like string to set monitors running time.\n          :max-retry-times    max retry times to monitor when found error.\n                                         When finding monito error,we will try it again at once.If the retry times is over this value,then we send alerts.\n         :enable-control-logging   whether to enable clojure-control logging.\n\n An example:\n\n     (start-monitor\n           :monitors [:mysql-monitor]\n           :alerts [ (mail :from \"alert@app.com\" to: \"me@app.com\")]\n           :cron   \"* */10 * * * ?\")\n",
   :var-type "macro",
   :line 120,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([]),
   :name "stop-monitors",
   :namespace "clj.monitor.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.core-api.html#clj.monitor.core/stop-monitors",
   :doc "Stop monitors",
   :var-type "function",
   :line 179,
   :file "src/clj/monitor/core.clj"}
  {:arglists ([n]),
   :name "init-scheduler",
   :namespace "clj.monitor.timer",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.timer-api.html#clj.monitor.timer/init-scheduler",
   :doc "Initialize a quartz scheduler",
   :var-type "function",
   :line 7,
   :file "src/clj/monitor/timer.clj"}
  {:arglists ([s f cron]),
   :name "schedule-task",
   :namespace "clj.monitor.timer",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.timer-api.html#clj.monitor.timer/schedule-task",
   :doc "Schedule a function with crontab-like string",
   :var-type "function",
   :line 23,
   :file "src/clj/monitor/timer.clj"}
  {:arglists ([s]),
   :name "start-scheduler",
   :namespace "clj.monitor.timer",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.timer-api.html#clj.monitor.timer/start-scheduler",
   :doc "Start the quartz scheduler",
   :var-type "function",
   :line 31,
   :file "src/clj/monitor/timer.clj"}
  {:arglists ([s]),
   :name "stop-scheduler",
   :namespace "clj.monitor.timer",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/clj.monitor.timer-api.html#clj.monitor.timer/stop-scheduler",
   :doc "Stop the quartz scheduler",
   :var-type "function",
   :line 36,
   :file "src/clj/monitor/timer.clj"})}
