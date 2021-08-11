(ns bienvenides.utils
  (:require
   [clojure.string :as str]))

(defn hash->name [hash]
  (let [raw (if hash (subs hash 1) "")]
    (->> (str/split raw #"%20")
         (filter (partial not= "")))))
