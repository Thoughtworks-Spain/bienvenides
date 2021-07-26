(ns bienvenides.events-test
  (:require [cljs.test :refer [deftest is]]))

(deftest square-test
  (is (= 4 (* 2 2))))

(deftest failing-test
  (is (= 0 1)))
