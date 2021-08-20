(ns bienvenides.encoding
  (:require [leipzig.melody :as melody]
            [leipzig.scale :as scale]
            [clojure.string :as str]
            [cljs.pprint :as pprint]))

(defn encode-pitch [persons-name _]
  (letfn [(index [c] (- (pprint/char-code c) (pprint/char-code \a)))]
    (->> persons-name
      (map index)
      (map #(mod % 5)))))

(defn is-vowel? [letter]
  (#{\a \e \i \o \u} letter))

(defn encode-one-duration [letter {:keys [duration]}]
  (if (is-vowel? letter)
    (:vowel duration 1)
    (:consonant duration 0.5)))

(defn encode-duration [persons-name encoding-options]
  (map #(encode-one-duration % encoding-options) persons-name))

(defn encode-one [persons-name encoding-options]
  (let [pitches (encode-pitch persons-name encoding-options)
        durations (encode-duration persons-name encoding-options)]
    (melody/phrase durations pitches)))

(defn set-indexes
  "Given the index of the name and an encoded name, set's `name-index` and
  `letter-index` for each note in each name."
  [name-index encoded-name]
  (map-indexed
   (fn [letter-index note]
     (assoc note
            :bienvenides/name-index name-index
            :bienvenides/letter-index letter-index))
   encoded-name))

(defn encode [names encoding-options]
  (->> names
       (map str/lower-case)
       (map #(encode-one % encoding-options))
       (map #(->> %2 (melody/where :pitch (scale/from (* 5 %1)))) (range))
       (map-indexed set-indexes)
       (reduce melody/with)))
