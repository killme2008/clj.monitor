(ns clj.monitor.timer
  #^{:doc "Clojure monitor timer"
     :author "dennis<xzhuang@avos.com>"}
  (:import (org.quartz JobDetail Scheduler Trigger CronScheduleBuilder JobBuilder TriggerBuilder Job JobDataMap JobExecutionContext)
           (org.quartz.impl DirectSchedulerFactory)))

(defn init-scheduler
  "Initialize a quartz scheduler"
  [n]
  (when (.. DirectSchedulerFactory (getInstance) (getAllSchedulers) (isEmpty))
    (.. DirectSchedulerFactory (getInstance) (createVolatileScheduler n)))
  (.. DirectSchedulerFactory (getInstance) (getScheduler)))

(def ^:private FUN "quartz-fn")

(deftype FunctionJob [] Job
         (execute [this ctx]
           (let [^JobDataMap m (.. ctx (getJobDetail) (getJobDataMap))
                 f (.get m FUN)]
             (when f
               (f)))))

(defn schedule-task
  "Schedule a function with crontab-like string"
  [^Scheduler s f ^String cron]
  (let [job (.. (JobBuilder/newJob FunctionJob) (build))
        trigger (.. (TriggerBuilder/newTrigger) (withSchedule (CronScheduleBuilder/cronSchedule cron)) (forJob job) (build))]
    (.. job (getJobDataMap) (put FUN f))
    (. s (scheduleJob job trigger))))

(defn start-scheduler
  "Start the quartz scheduler"
  [^Scheduler s]
  (.start s))
