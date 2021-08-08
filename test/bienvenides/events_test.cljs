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

(deftest plays-the-name
  (is (= {:play {:notes [{:pitch 0 :time 0 :duration 1}
                         {:pitch 2 :time 1 :duration 0.5}
                         {:pitch 4 :time 1.5 :duration 1}]
                 :audio-context 'TheContext}}
         (events/play {:db {:audio-context 'TheContext
                            :name ["ace"]}} nil))))
