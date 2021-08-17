(ns bienvenides.encoding-test
  (:require
    [cljs.test :refer [deftest is]]
    [bienvenides.encoding :as encoding]))

(deftest initializes-to-empty
  (is (= []
         (encoding/encode []))))

(deftest starts-at-a
  (is (= [{:pitch 0 :duration 1 :time 0 :bienvenides/name-index 0 :bienvenides/letter-index 0}
          {:pitch 1 :duration 0.5 :time 1 :bienvenides/name-index 0 :bienvenides/letter-index 1}
          {:pitch 2 :duration 0.5 :time 1.5 :bienvenides/name-index 0 :bienvenides/letter-index 2}]
         (encoding/encode ["abc"]))))

(deftest cycles-around-at-f
  (is (= [{:pitch 4 :duration 1 :time 0 :bienvenides/name-index 0 :bienvenides/letter-index 0}
          {:pitch 0 :duration 0.5 :time 1 :bienvenides/name-index 0 :bienvenides/letter-index 1}
          {:pitch 1 :duration 0.5 :time 1.5 :bienvenides/name-index 0 :bienvenides/letter-index 2}]
         (encoding/encode ["efg"]))))

(deftest treats-multiple-words-as-multiple-parts-in-increasing-octaves
  (is (= [{:pitch 0 :duration 1 :time 0 :bienvenides/name-index 0 :bienvenides/letter-index 0}     ; a
          {:pitch 7 :duration 0.5 :time 0 :bienvenides/name-index 1 :bienvenides/letter-index 0}   ; c

          {:pitch 8 :duration 0.5 :time 0.5 :bienvenides/name-index 1 :bienvenides/letter-index 1} ; d
          {:pitch 1 :duration 0.5 :time 1 :bienvenides/name-index 0 :bienvenides/letter-index 1}]  ; b
         (encoding/encode ["ab" "cd"]))))

(deftest treats-uppercase-the-same-as-lowercase
  (is (= [{:pitch 0 :duration 1 :time 0 :bienvenides/name-index 0 :bienvenides/letter-index 0}
          {:pitch 1 :duration 0.5 :time 1 :bienvenides/name-index 0 :bienvenides/letter-index 1}
          {:pitch 2 :duration 0.5 :time 1.5 :bienvenides/name-index 0 :bienvenides/letter-index 2}]
         (encoding/encode ["ABC"]))))
