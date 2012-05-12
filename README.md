#Introduction

Monitoring applications in clojure based on [clojure-control](https://github.com/killme2008/clojure-control/).

#Leiningen dependency

    [clj.monitor "1.0.0-SNAPSHOT"]

#Getting started

First,we define a cluster using [clojure-control]():

		 (defcluster mysql
		    :clients [{:user "deploy" :host "mysql1"}])

Second,we define a monitor for mysql cluster:

        (defmonitor mysql-monitor
		    ;;Tasks to monitor mysql,we just ping mysql
		    :tasks [(ping-mysql "root" "password")]
			;;Mysql clusters for monitoring
			:clusters [:mysql])

The  `mysql-monitor` try to ping mysql with `root` and `passowrd`,and execute the task on `mysql` cluster.

At last,start the monitors:

        (start-monitors
             :cron "* 0/5 * * * ?"
             :alerts [(mail :from "alert@app.com" :to "yourname@app.com")]
             :monitors [mysql-monitor])

If pinging mysql fails,it will send an alert email to address `yourname@app.com` from ` alert@app.com`.

#License

MIT licensed,the same with [clojure-control](https://github.com/killme2008/clojure-control/).





