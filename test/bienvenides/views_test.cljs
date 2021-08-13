(ns bienvenides.views-test
  (:require [bienvenides.views :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-current-page-core

  (testing "Renders not found if no match"
    (is (= [sut/not-found {:routing-match nil}]
           (sut/current-page-core {:routing-match nil}))))

  (testing "Renders view if match"
    (let [match {:data {:view sut/url-generator}}]
      (is (= [sut/url-generator {:routing-match match}]
             (sut/current-page-core {:routing-match match}))))))


(deftest test-main-panel

  (testing "extracts query-param from routing match"
    (let [routing-match {:query-params {:name "Foo Bar Baz"}}]
      (is (= [sut/main-panel-core {:name "Foo Bar Baz"}]
             (sut/main-panel {:routing-match routing-match})))))

  (testing "Defaults to Anom"
    (let [routing-match {}]
      (is (= [sut/main-panel-core {:name "Anom"}]
             (sut/main-panel {:routing-match routing-match}))))))
