(ns clj.monitor.example
  (:use [clj.monitor.core]
        [control.core]
        [clj.monitor.tasks]))

;;define a mysql cluster
(defcluster mysql
  :clients [{:user "deploy" :host "mysql.app.com"}])

;;define a monitor for mysql cluster
(defmonitor mysql-monitor
  ;;Tasks to monitor mysql,we just ping mysql and make sure that average load in 5 minutes is less than 3
  :tasks [(ping-mysql "root" "password")
             (system-load :5 3)]
  ;Mysql clusters for monitoring
  :clusters [:mysql])

;;start monitors
(start-monitors
 ;;Run monitors every five minutes
 :cron "* 0/5 * * * ?"
 ;;Send alert messages to killme2008@gmail.com when monitor fail.
 :alerts [(mail :from "alert@app.com" :to "yourname@app.com")]
 ;;Monitors that are defined by defmonitor
 :monitors [mysql-monitor])

