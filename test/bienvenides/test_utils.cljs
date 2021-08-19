(ns bienvenides.test-utils
  (:require [bienvenides.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(defprotocol IFnStub
  (store-call [this args])
  (get-calls [this]))

(defn new-stub
  "Returns a new callable stub."
  ([] (new-stub {}))
  ([{:keys [fn] :or {fn (constantly nil)}}]
   (let [calls (atom [])]
     (reify
       IFnStub
       (store-call [this args] (swap! calls conj args))
       (get-calls [this] @calls)
       IFn
       (-invoke [this]
         (store-call this [])
         (apply fn []))
       (-invoke [this a]
         (store-call this [a])
         (apply fn [a]))
       (-invoke [this a b]
         (store-call this [a b])
         (apply fn [a b]))
       (-invoke [this a b c]
         (store-call this [a b c])
         (apply fn [a b c]))
       (-invoke [this a b c d]
         (store-call this [a b c d])
         (apply fn [a b c d]))
       (-invoke [this a b c d e]
         (store-call this [a b c d e])
         (apply fn [a b c d e]))
       (-invoke [this a b c d e f]
         (store-call this [a b c d e f])
         (apply fn [a b c d e f]))
       (-invoke [this a b c d e f g]
         (store-call this [a b c d e f g])
         (apply fn [a b c d e f g]))
       (-invoke [this a b c d e f g h]
         (store-call this [a b c d e f g h])
         (apply fn [a b c d e f g h]))))))
