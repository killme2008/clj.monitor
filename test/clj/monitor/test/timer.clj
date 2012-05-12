(ns clj.monitor.test.timer
  (:use [clj.monitor.timer])
  (:use [clojure.test]))

(deftest test-scheduler
  (let [sc (init-scheduler 1)
        counter (atom 0)]
    (try
      (schedule-task sc #(swap! counter inc) "*/1 * * * * ?")
      (start-scheduler sc)
      (Thread/sleep 5000)
      (is (= 6 @counter))
      (finally
       (stop-scheduler sc)))
    ))


