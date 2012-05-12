(defproject clj.monitor "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [control "0.3.8"]
                 [com.draines/postal "1.6.0"]
                 [org.quartz-scheduler/quartz "2.1.4"]
                 [org.clojure/tools.logging "0.2.3"]
                 [clj-redis "0.0.12"]]
  :dev-dependencies [[log4j/log4j "1.2.16"]
                     [org.slf4j/slf4j-log4j12 "1.5.6"]]
  :profiles {:dev {:resources-path ["dev"]}})