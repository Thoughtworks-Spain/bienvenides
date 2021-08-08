(ns bienvenides.song
  (:require [overtone.live :refer :all]
            [leipzig.melody :refer :all]
            [leipzig.scale :as scale]
            [leipzig.live :as live]
            [leipzig.chord :as chord]
            [leipzig.temperament :as temperament]
            [clojure.string :as string]))

; Instruments
(definst bass [freq 110 dur 1.0 volume 1.0]
  (-> (saw freq)
      (+ (square (* (+ 1 (* 0.005 (sin-osc 1))) freq)))
      (* (sin-osc 4))
      (rlpf (line:kr (* 5 freq) freq 4) 0.3)
      (free-verb :damp 0.3 :mix 0.4 :room 0.8)
      (* (env-gen (adsr 0.01 0.1 0.6 0.1) (line:kr 1 0 dur) :action FREE))
      (* volume)))

; Arrangement
(defmethod live/play-note :default
  [{hertz :pitch duration :duration}]
  (bass hertz duration 0.7))

(defn is-vowel [c]
  (#{\a \e \i \o \u} c))

(defn encode-pitch [persons-name]
  (->> persons-name
       (map int)
       (map #(mod % 9))
       (map dec)))

(defn encode-duration [c]
  (let [default 1/2
       vowel (if (is-vowel c) (* 2 default) default)
       upper (if (Character/isUpperCase c) (* 2 vowel) vowel)]
    upper))

(defn encode-one [persons-name]
  (let [pitches (encode-pitch persons-name)
        durations (->> persons-name
                       (map encode-duration))]
    (phrase durations pitches)))

(defn encode [names]
  (->> names
       (map encode-one)
       (map #(->> %2 (where :pitch (scale/from %1) )) (map (partial * 5) (range)))
       (reduce with)))

; Composition
(def track
  (let [tune (->>
               (string/split "Cristina Copete" #" ")
               ;(string/split "Jorge Agudo Praena" #" ")
               ;(string/split "Christopher Thomas Ford" #" ")
               ;(string/split "Florence Ivy June Adlem" #" ")
               ;(string/split "Cristobal Garcia Garcia" #" ")
               ;(string/split "Jennifer Louise Adlem" #" ")
               ;(string/split "Mansi Shah" #" ")
               ;(string/split "Karin Verloop" #" ")
               encode)]
   (->>
     (cut 4 tune)
     (then (cut 4 tune))
     (then (cut 8 tune))
     (times 2)
     (with (phrase (repeat 4) [0 -1 -2 -3 0 -1 -2 -5]))
     (where :pitch (comp temperament/equal scale/C scale/pentatonic))
     (tempo (bpm 120)))))

(comment
  ; Play it once
  (live/play track)

  ; Loop the track, allowing live editing.
  (live/jam (var track))
  (live/stop)
)
