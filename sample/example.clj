(ns clj.monitor.example
  (:use [clj.monitor.core]
        [control.core]
        [clj.monitor.tasks]))

;;define a mysql cluster
(defcluster mysql
  :clients [{:user "deploy" :host "mysql1"}])

;;define a monitor for mysql cluster
(defmonitor mysql-monitor
  :tasks [(ping-mysql "root" "password")]
  :clusters [:mysql]
  :cron "0 0 6,18 * * ?")

;;start monitors
(start-monitors
 :alerts [(mail :from "killme2008@avos.com" :to "xzhuang@avos.com")]
 :monitors [mysql-monitor])

