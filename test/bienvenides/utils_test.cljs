(ns bienvenides.utils-test
  (:require [bienvenides.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest initializes-to-empty
  (is (= []
         (sut/hash->name nil))))

(deftest splits-hash
  (is (= ["Foo" "Bar"]
         (sut/hash->name "#Foo%20Bar"))))

(deftest ignores-extra-spaces
  (is (= ["Foo" "Bar"]
         (sut/hash->name "#Foo%20%20Bar%20"))))
