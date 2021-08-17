(ns bienvenides.utils
  (:require
   [clojure.string :as str]
   [clojure.core.async :as async]))

(defn parse-names
  "Parses a name before it's used to generate a music"
  [name]
  (->> (str/split name #" ")
       (filter (partial not= ""))))

(defn generate-url
  "Given a name, generates an welcome url"
  [name]
  (str js/window.location.origin js/window.location.pathname "#/?name=" (js/encodeURIComponent name)))

(defn timeout-seconds
  "Wait x seconds"
  [s]
  (async/timeout (* 1000 s)))
