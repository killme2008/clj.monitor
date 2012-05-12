(ns clj.monitor.test.alerts
  (:use [clj.monitor.alerts])
  (:use [clojure.test]))

(deftest install-get-alert-fn
  (is (nil? (get-alert-fn :test1)))
  (is (nil? (get-alert-fn :test2)))
  (install-alert-fn :test1 1 :test2 2)
  (is (= 1 (get-alert-fn :test1)))
  (is (= 2 (get-alert-fn :test2)))
  (clear-alert-fns)
    (is (nil? (get-alert-fn :test1)))
  (is (nil? (get-alert-fn :test2))))
