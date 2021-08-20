(ns bienvenides.events-test
  (:require
    [cljs.test :refer [deftest is testing]]
    [bienvenides.events :as events]
    [bienvenides.test-utils :as tu]
    [bienvenides.synth :as synth]
    [bienvenides.encoding :as encoding]))

(deftest initializes-audio-context
  (is (= :foo
         (events/initialize-audio-context {:audio-context :foo} nil))))

(deftest test-play-fx

  (testing "Calls synth/play"
    (let [notes [{:pitch 0 :time 0 :duration 1 :bienvenides/name-index 0 :bienvenides/letter-index 0}]
          audio-context ::audio-context
          play-options {:beats 10}
          play (tu/new-stub)]
      (with-redefs [synth/play play]
        (events/play-fx {:notes notes :audio-context audio-context :play-options play-options}))
      (is (= notes (-> play tu/get-calls first (get 0))))
      (is (= audio-context (-> play tu/get-calls first (get 1))))
      (is (= play-options (-> play tu/get-calls first (get 2) (dissoc :register-note!))))
      (is (ifn? (-> play tu/get-calls first (get 2) :register-note!))))))

(deftest play

  (testing "Plays the name"
    (is (= {:play {:notes [{:pitch 0 :time 0 :duration 1 :bienvenides/name-index 0 :bienvenides/letter-index 0}
                           {:pitch 2 :time 1 :duration 0.5 :bienvenides/name-index 0 :bienvenides/letter-index 1}
                           {:pitch 4 :time 1.5 :duration 1 :bienvenides/name-index 0 :bienvenides/letter-index 2}]
                   :audio-context 'TheContext
                   :play-options {:beats 120}}}
           (events/play {:db {:audio-context 'TheContext
                              :play-options {:beats 120}}}
                        [::events/play ["Ace"]]))))

  (testing "Passes encoding-options from db"
    (let [names ["A"]
          encoding-options {:duration {:vowel 1
                                       :consonant 2}}
          db {:encoding-options encoding-options
              :audio-context ::audio-context}
          cofx {:db db}
          event [::events/play ["A"]]
          encode-stub (tu/new-stub {:fn #(do ::encoded)})]
      (with-redefs [encoding/encode encode-stub]
        (let [result (events/play cofx event)]
          (is (= {:play {:notes ::encoded
                         :audio-context ::audio-context
                         :play-options nil}}
                 result))
          (is (= [[names encoding-options]]
                 (tu/get-calls encode-stub))))))))

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
