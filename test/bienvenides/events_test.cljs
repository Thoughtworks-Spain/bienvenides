(ns bienvenides.events-test
  (:require
    [cljs.test :refer [deftest is testing]]
    [bienvenides.events :as events]))

(deftest initializes-audio-context
  (is (= :foo
         (events/initialize-audio-context {:audio-context :foo} nil))))

(deftest play
  (testing "Plays the name"
    (is (= {:play {:notes [{:pitch 0 :time 0 :duration 1}
                           {:pitch 2 :time 1 :duration 0.5}
                           {:pitch 4 :time 1.5 :duration 1}]
                   :audio-context 'TheContext}}
           (events/play {:db {:audio-context 'TheContext}}
                        [::events/play ["Ace"]])))))

(deftest test-new-routing-match
  (is (= {:db {::foo 1
               :routing-match ::match2}}
         (events/new-routing-match {:db {::foo 1
                                         :routing-match ::match1}}
                                   [::events/new-routing-match ::match2]))))
