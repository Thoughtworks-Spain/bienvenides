(ns bienvenides.encoding-test
  (:require
    [cljs.test :refer [deftest is]]
    [bienvenides.encoding :as encoding]))

(deftest initializes-to-empty
  (is (= []
         (encoding/encode []))))

(deftest starts-at-a
  (is (= [{:pitch 0 :duration 1 :time 0}
          {:pitch 1 :duration 1 :time 1}
          {:pitch 2 :duration 1 :time 2}]
         (encoding/encode ["abc"]))))

(deftest cycles-around-at-f
  (is (= [{:pitch 4 :duration 1 :time 0}
          {:pitch 0 :duration 1 :time 1}
          {:pitch 1 :duration 1 :time 2}]
         (encoding/encode ["efg"]))))

(deftest treats-multiple-words-as-multiple-parts-in-increasing-octaves
  (is (= [
          {:pitch 0 :duration 1 :time 0}
          {:pitch 2 :duration 1 :time 0}
          {:pitch 4 :duration 1 :time 0}

          {:pitch 6 :duration 1 :time 1}
          {:pitch 8 :duration 1 :time 1}
          {:pitch 5 :duration 1 :time 1}]
         (encoding/encode ["ab" "cd" "ef"]))))

(deftest treats-uppercase-the-same-as-lowercase
  (is (= [{:pitch 0 :duration 1 :time 0}
          {:pitch 1 :duration 1 :time 1}
          {:pitch 2 :duration 1 :time 2}]
         (encoding/encode ["ABC"]))))
