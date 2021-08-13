(ns bienvenides.views-test
  (:require [bienvenides.views :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-current-page-core

  (testing "Renders not found if no match"
    (is (= [sut/not-found nil]
           (sut/current-page-core {:routing-match nil}))))

  (testing "Renders view if match"
    (let [match {:data {:view sut/url-generator}}]
      (is (= [sut/url-generator match]
             (sut/current-page-core {:routing-match match}))))))
