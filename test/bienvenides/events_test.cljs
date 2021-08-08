(ns bienvenides.events-test
  (:require
    [cljs.test :refer [deftest is]]
    [bienvenides.events :as events]))

(deftest initializes-to-empty
  (is (= []
         (events/initialize-name {:hash-fragment nil} nil))))

(deftest splits-hash-fragment
  (is (= ["Foo" "Bar"]
         (events/initialize-name {:hash-fragment "#Foo%20Bar"} nil))))

(deftest ignores-extra-spaces
  (is (= ["Foo" "Bar"]
         (events/initialize-name {:hash-fragment "#Foo%20%20Bar%20"} nil))))

(deftest initializes-audio-context
  (is (= :foo
         (events/initialize-audio-context {:audio-context :foo} nil))))

(deftest plays-a-log-message
  (is (= {:play {:notes [{:duration 1 :pitch 0 :time 0}]
                 :audio-context 'TheContext}}
         (events/play {:db {:audio-context 'TheContext}} nil))))
