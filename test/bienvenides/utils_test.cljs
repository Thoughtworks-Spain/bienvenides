(ns bienvenides.utils-test
  (:require [bienvenides.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))


(deftest test-parse-names

  (testing "Empty if nil"
    (is (= []
           (sut/parse-names nil))))

  (testing "Splits by spaces"
    (is (= ["Foo" "Bar"]
           (sut/parse-names "Foo Bar"))))

  (testing "Ignores extra spaces"
    (is (= ["Foo" "Bar"]
           (sut/parse-names "Foo   Bar ")))))
