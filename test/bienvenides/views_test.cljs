(ns bienvenides.views-test
  (:require [bienvenides.views :as sut]
            [re-frame.core :as re-frame]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [bienvenides.subs :as subs]))

(def a-note {:bienvenides/letter-index 1 :bienvenides/name-index 0})

(deftest test-current-page-core

  (testing "Renders not found if no match"
    (is (= [sut/not-found {:routing-match nil}]
           (sut/current-page-core {:routing-match nil}))))

  (testing "Renders view if match"
    (let [match {:data {:view sut/url-generator}}]
      (is (= [sut/url-generator {:routing-match match}]
             (sut/current-page-core {:routing-match match}))))))


(deftest test-main-panel-name

  (testing "Renders the name with no active letter if no current note"
    (let [current-notes #{}
          names ["Foo" "Bar"]]
      (is (= [:span.main-panel__full-name
              [[:span.main-panel__single-name {:key 0}
                [[:span {:key "00F" :class sut/letter-class} "F"]
                 [:span {:key "01o" :class sut/letter-class} "o"]
                 [:span {:key "02o" :class sut/letter-class} "o"]]]
               [:span.main-panel__single-name {:key 1}
                [[:span {:key "10B" :class sut/letter-class} "B"]
                 [:span {:key "11a" :class sut/letter-class} "a"]
                 [:span {:key "12r" :class sut/letter-class} "r"]]]]]
             (sut/main-panel-name {:names names :current-notes current-notes})))))

  (testing "Renders notes with active class if they are active."
    (let [current-notes #{a-note}
          names ["Foo"]]
      (is (= [:span.main-panel__full-name
              [[:span.main-panel__single-name {:key 0}
                [[:span {:key "00F" :class sut/letter-class} "F"]
                 [:span {:key "01o" :class sut/letter-class-active} "o"]
                 [:span {:key "02o" :class sut/letter-class} "o"]]]]]
             (sut/main-panel-name {:names names :current-notes current-notes}))))))

(deftest test-main-panel

  (testing "Extracts query-param from routing match."
    (let [routing-match {:query-params {:name "Foo Bar Baz"}}]
      (is (= [sut/main-panel-core {:names ["Foo" "Bar" "Baz"]
                                   :current-notes #{}
                                   :play-options subs/DEFAULT_PLAY_OPTIONS}]
             (sut/main-panel {:routing-match routing-match})))))

  (testing "Defaults name to Anom"
    (let [routing-match {}]
      (is (= [sut/main-panel-core {:names ["Anom"]
                                   :current-notes #{}
                                   :play-options subs/DEFAULT_PLAY_OPTIONS}]
             (sut/main-panel {:routing-match routing-match})))))

  (testing "Uses subscription for current-notes and play-options."
    (with-redefs [re-frame/subscribe (fn [[k]]
                                       (atom
                                        (case k
                                          ::subs/current-notes #{a-note}
                                          ::subs/play-options {:beats 120})))]
      (is (= [sut/main-panel-core {:names ["Anom"]
                                   :current-notes #{a-note}
                                   :play-options {:beats 120}}]
             (sut/main-panel {:routing-match nil}))))))
