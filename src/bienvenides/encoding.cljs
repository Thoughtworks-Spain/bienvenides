(ns bienvenides.encoding
  (:require [leipzig.melody :as melody]
            [leipzig.scale :as scale]
            [clojure.string :as str]
            [cljs.pprint :as pprint]))

(defn encode-pitch [persons-name]
  (letfn [(index [c] (- (pprint/char-code c) (pprint/char-code \a)))]
    (->> persons-name
      (map index)
      (map #(mod % 5)))))

(defn is-vowel? [letter]
  (#{\a \e \i \o \u} letter))

(defn encode-one-duration [letter]
  (if (is-vowel? letter)
    1
    0.5))

(defn encode-duration [persons-name]
  (map encode-one-duration persons-name))

(defn encode-one [persons-name]
  (let [pitches (encode-pitch persons-name)
        durations (encode-duration persons-name)]
    (melody/phrase durations pitches)))

(defn set-indices
  "Given the index of the name and an encoded name, set's `name-index` and
  `letter-index` for each note in each name."
  [name-index encoded-name]
  (map-indexed
   (fn [letter-index note]
     (assoc note
            :bienvenides/name-index name-index
            :bienvenides/letter-index letter-index))
   encoded-name))

(defn encode [names]
  (->> names
       (map str/lower-case)
       (map encode-one)
       (map-indexed #(->> %2 (melody/where :pitch (scale/from (* 5 %1)))))
       (map-indexed set-indices)
       (reduce melody/with)))
