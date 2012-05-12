(defproject clj.monitor "1.0.0-beta"
  :description "Monitoring applications in clojure based on clojure-control."
  :url "https://github.com/killme2008/clj.monitor"
  :author "dennis zhuang(killme2008@gmail.com)"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [control "0.3.8"]
                 [com.draines/postal "1.6.0"]
                 [org.quartz-scheduler/quartz "2.1.4"]
                 [org.clojure/tools.logging "0.2.3"]
                 [clj-redis "0.0.12"]]
  :dev-dependencies [[log4j/log4j "1.2.16"]
                     [lein-autodoc "0.9.0"]
                     [org.slf4j/slf4j-log4j12 "1.5.6"]]
  :profiles {:dev {:resources-path ["dev"]}})