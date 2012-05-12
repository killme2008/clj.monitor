#Introduction

Monitoring applications in clojure based on [clojure-control](https://github.com/killme2008/clojure-control/).

#Leiningen dependency

    [clj.monitor "1.0.0-beta"]

#Setup SSH

You must install a ssh agent on the machine where your `clj.monitor` runs on.
And then ,you must setup ssh to make sure logining your monitored application server  from  `clj.monitor` machine without typing password.Please see [HOWTO: set up ssh keys](http://pkeck.myweb.uga.edu/ssh/).

#Getting started

Use `clj.monitor` in your clojure ns:

    	 (ns clj.monitor.example
             (:use [clj.monitor.core]
                      [control.core]
                      [clj.monitor.tasks]))


First,we define a cluster using [clojure-control](https://github.com/killme2008/clojure-control/) `defcluster`:

		 (defcluster mysql
		    :clients [{:user "deploy" :host "mysql.app.com"}])

Second,we define a monitor for `mysql` cluster:

        (defmonitor mysql-monitor
		     ;;Tasks to monitor mysql,we just ping mysql and make sure that average load in 5 minutes is less than 3
    		 :tasks [(ping-mysql "root" "password")
                         (system-load :5 3)]
			 ;;Mysql clusters for monitoring
			 :clusters [:mysql])

The  `mysql-monitor` try to ping mysql with `root` and `passowrd` and get average system load in 5 minutes,and execute the task on `mysql` cluster.

At last,start the monitors:

        (start-monitors
             :cron "* 0/5 * * * ?"
             :alerts [(mail :from "alert@app.com" :to "yourname@app.com")]
             :monitors [mysql-monitor])

If pinging mysql fails or mysql machine's average load in 5 minutes is greater than 3,it will send an alert email to address `yourname@app.com` from ` alert@app.com`.Monitors will run every five minutes set by `* 0/5 * * * ?` -- a crontab-like string using [Quartz](http://quartz-scheduler.org/).

The alert message is like:
      
      [Alert]:
	  {:msyql-monitor
	    {"(system-load :5 3)"    {"mysql.app.com" false},
         "(ping-mysql "root" "password)"    {"msyql.app.com" true}}}

It means that system load in 5 minutes is greater than 3(`false),but mysql is still alive(`true`).

#Pre-defined tasks and alerts

##Tasks

* (ping-mysql "user" "pass"): `mysqladmin -u user -p'pass' ping`, make sure that returns mysql is alive.
* (mysql-slave-status "user" "pass"): `mysql -u user -p'pass' -e 'show slave status\G'` ,and make sure that it returns two `YES`.
* (count-process "process" min): `ps aux |grep [process] |grep -v -c grep`,make sure the returned number is greater than min.
* (service-status "app" sudo "error"): `sudo /etc/init.d/app status` ,make sure the returned output doesn't contains the error string.`sudo` is a boolean value to set whether running with sudo.
* (ping-redis "redis://host:port"): use redis ping command to ping redis,make sure it returns `PONG`.
* (system-load kind max): `uptime`,  the `kind` is `:1`,`:5` or `:15`,make sure that system average load in 1,5,15 minutes is less than max value.

You can define your own tasks by `deftask` in clojure-control,pelase see [Define tasks](https://github.com/killme2008/clojure-control/wiki/Define-tasks).

##Alerts

* (mail :from from :to to [:subject subject :body body]): send alert messages as email.When you set body,the email body will be replaced by your setting instead of alert messages.
* (console): write alert messages to console
* (log):  write alert messages to log using tools.logging.

You can install your own alert function by `clj.monitor.alerts/install-alert-fn`:

    	  (use '[clj.monitor.alerts :only [install-alert-fn]])
          (install-alert-fn :name (fn [rt & args] (println rt)))

#API document

* [API document](http://fnil.net/clj.monitor)

#License

MIT licensed,the same with [clojure-control](https://github.com/killme2008/clojure-control/).




