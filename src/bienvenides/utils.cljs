(ns bienvenides.utils
  (:require
   [clojure.string :as str]))

(defn parse-name
  "Parses a name before it's used to generate a music"
  [name]
  (->> (str/split name #" ")
       (filter (partial not= ""))))
