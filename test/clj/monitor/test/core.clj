(ns clj.monitor.test.core
  (:use [clj.monitor.core])
  (:use [clojure.test]))
(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
	 ~@tests))

(deftest install-get-clear-monitors
  (is (nil? (get-monitor :test)))
  (install-monitor :test {:name "test"})
  (is (= {:name "test"}(get-monitor :test)))
  (clear-monitors)
  (is (nil? (get-monitor :test))))

(deftest test-defmonitor
  (is (nil? (get-monitor :test-monitor)))
  (defmonitor test-monitor
    :clusters [:mysql]
    :host "hello@test"
    :tasks [(ping-mysql "root" "pass")])
  (let [m (get-monitor :test-monitor)]
    (is (= [:mysql] (:clusters m)))
    (is (= "hello@test" (:host m)))
    (is (= '[(ping-mysql "root" "pass")] (:tasks m))))
  (is (thrown-with-msg? IllegalArgumentException #"Only these options" (defmonitor  hello :fuck 1))))


(with-private-fns [clj.monitor.core [pick-error-monitors]]
  (deftest test-pick-error-monitors
    (is (= {:monitor1 { :task1 {:host1 true :host2 true} :task2 {:host1 true :host2 false}}
            :monitor2 { :exception "error"}}  (pick-error-monitors {:monitor1 { :task1 {:host1 true :host2 true} :task2 {:host1 true :host2 false}}
                                                                    :monitor2 { :exception "error"}
                                                                    :monitor {:task1 {:host1 true :host3 true}}})))))








