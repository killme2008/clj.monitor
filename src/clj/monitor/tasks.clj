(ns clj.monitor.tasks
  #^{
     :doc "Some pre-defined tasks for monitors"
     :author "dennis<xzhuang@avos.com>"}
  (:import (redis.clients.jedis Jedis))
  (:use [control.core]
        [clojure.walk :only [walk postwalk]])
  (:require [clj-redis.client :as redis]))

(deftask ping-mysql
  "Ping if mysql is alive.Equals to:

       mysqladmin -u user -p'pass' ping
  "
  [user pass]
  (let [stdout (:stdout (ssh (format "mysqladmin -u %s  -p'%s' ping" user pass)))]
    (.contains ^String stdout "is alive")))

(deftask mysql-slave-status
  "Check if mysql slave status is ok.Equals to:

          mysql -u user -p'pass' -e 'show slave status\\G'

   Check if the output contains two \"YES\"
  "
  [user pass]
  (let [stdout (:stdout  (ssh (format "mysql -u %s -p'%s' -e \"show slave status\\G\" " user pass)))]
    (= (count (re-seq #"Yes" ^String stdout)) 2)))

(defn- cast-int
  "Cast a string to int,returns 0 if string is blank"
  [key]
  (if (or (nil? key) (= (count key) 0))
    0
    (Integer/valueOf (.trim key))))

(deftask count-process
  "Count process number is right.Equals to :

           ps aux |grep [process] |grep -v -c grep
  "
  [process min]
  (let [rt (cast-int (:stdout (ssh (format "ps aux|grep  %s |grep -v -c grep" process))))]
    (>= rt min)))

(deftask service-status
  "Check if a daemon service is running.Equals to:

             sudo /etc/init.d/app status

   Then check if the output contains the error message.
 "
  [app sudo error]
  (let [rt (ssh (str "/etc/init.d/" app " status") :sudo (or sudo true))
        out (:stdout rt)
        stderr (.trim (or (:stderr rt) ""))
        status (:status rt)]
    (and (zero? status) (not (> (.indexOf ^String out (or error "not running")) 0)) (empty? stderr))))

(deftask ping-redis
  "Ping redis to check it it is all right,use 'ping' protocol for redis.
   Argument could be a url string in the form of \"redis://host:port\" or a JRedis object.
 "
  [arg]
  (let [new-cli (instance? String arg)
        cli (if new-cli
              (redis/init :url arg)
              arg)]
    (try
      (= "PONG" (redis/ping cli))
      (finally
       (when new-cli
         (.quit ^Jedis cli))))))

(deftask system-load
  "Make sure that system load is less than max value ,valid kind including:
       :1      average load in 1 minute.
       :5      average load in 5 minutes.
       :15    average load in 15 mintues.
 "
  [kind max]
  (let [out (:stdout (ssh "uptime"))
        kind (keyword kind)
        loads (re-seq #"\d+\.\d+" out)]
    (when loads
      (let [kind-map (zipmap [:1 :5 :15] loads)]
        (<= (Double/valueOf (kind kind-map))  max)))))

(deftask tcp-states
  "Check TCP connection states number less than max value"
  [state max]
  (let [state (.toLowerCase ^String state)]
    (<=  (cast-int (:stdout (ssh (format "netstat -aN | grep 'tcp.*http.*%s' | wc -l" state)))) max)))
