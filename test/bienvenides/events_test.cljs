(ns bienvenides.events-test
  (:require
    [cljs.test :refer [deftest is testing]]
    [bienvenides.events :as events]))

(deftest initializes-audio-context
  (is (= :foo
         (events/initialize-audio-context {:audio-context :foo} nil))))

(deftest play
  (testing "Plays the name"
    (is (= {:play {:notes [{:pitch 0 :time 0 :duration 1 :bienvenides/name-index 0 :bienvenides/letter-index 0}
                           {:pitch 2 :time 1 :duration 0.5 :bienvenides/name-index 0 :bienvenides/letter-index 1}
                           {:pitch 4 :time 1.5 :duration 1 :bienvenides/name-index 0 :bienvenides/letter-index 2}]
                   :audio-context 'TheContext}}
           (events/play {:db {:audio-context 'TheContext}}
                        [::events/play ["Ace"]])))))

(deftest test-new-routing-match
  (is (= {:db {::foo 1
               :routing-match ::match2}}
         (events/new-routing-match {:db {::foo 1
                                         :routing-match ::match1}}
                                   [::events/new-routing-match ::match2]))))

(deftest test-note-started-playing
  (let [a-note {:pitch 0 :time 0 :duration 1 :bienvenides/name-index 1 :bienvenides/letter-index 1}
        another-note {:pitch 1 :time 1 :duration 1.5 :bienvenides/name-index 2 :bienvenides/letter-index 1}]

    (testing "Empty"
      (is (= {:db {:foo :bar
                   :current-notes #{a-note}}}
             (events/note-started-playing {:db {:foo :bar}}
                                          [::events/note-started-playing a-note]))))

    (testing "Not Empty"
      (is (= {:db {:current-notes #{a-note another-note}}}
             (events/note-started-playing {:db {:current-notes #{a-note}}}
                                          [::events/note-started-playing another-note]))))))

(deftest test-note-stopped-playing
  (let [a-note {:pitch 0 :time 0 :duration 1 :bienvenides/name-index 1 :bienvenides/letter-index 1}
        another-note {:pitch 1 :time 1 :duration 1.5 :bienvenides/name-index 2 :bienvenides/letter-index 1}]

    (testing "Empty"
      (is (= {:db {:foo :bar
                   :current-notes #{}}}
             (events/note-stopped-playing {:db {:foo :bar}}
                                          [::events/note-stopped-playing a-note]))))

    (testing "Not Empty"
      (is (= {:db {:current-notes #{another-note}}}
             (events/note-stopped-playing {:db {:current-notes #{a-note another-note}}}
                                          [::events/note-stopped-playing a-note]))))))
