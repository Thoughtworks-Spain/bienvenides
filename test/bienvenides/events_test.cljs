(ns bienvenides.events-test
  (:require
    [cljs.test :refer [deftest is]]
    [bienvenides.events :as events]))

(deftest initializes-to-empty
  (is (= {:name []}
         (events/initialize-db {:hash-fragment nil} nil))))

(deftest splits-hash-fragment
  (is (= {:name ["Foo" "Bar"]}
         (events/initialize-db {:hash-fragment "#Foo%20Bar"} nil))))

(deftest ignores-extra-spaces
  (is (= {:name ["Foo" "Bar"]}
         (events/initialize-db {:hash-fragment "#Foo%20%20Bar%20"} nil))))

(deftest plays-a-log-message
  (is (= {:log "Played!"}
         (events/play {:hash-fragment "#Foo%20%20Bar%20"} nil))))
