(ns clj.monitor.example
  (:use [clj.monitor.core]
        [control.core]
        [clj.monitor.tasks]))

;;define a mysql cluster
(defcluster mysql
  :clients [{:user "deploy" :host "mysql1"}])

;;define a monitor for mysql cluster
(defmonitor mysql-monitor
  ;;Tasks to monitor mysql,we just ping mysql
  :tasks [(ping-mysql "root" "password")]
  ;Mysql clusters for monitoring
  :clusters [:mysql])

;;start monitors
(start-monitors
 ;;Run monitors every five minutes
 :cron "* 0/5 * * * ?"
 ;;Send alert messages to killme2008@gmail.com when monitor fail.
 :alerts [(mail :from "killme2008@avos.com" :to "xzhuang@avos.com")]
 ;;Monitors that are defined by defmonitor
 :monitors [mysql-monitor])

