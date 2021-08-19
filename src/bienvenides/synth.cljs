(ns bienvenides.synth
  (:require
   [cljs-bach.synthesis :as cljb]
   [leipzig.temperament :as temperament]
   [leipzig.melody :as melody]
   [leipzig.scale :as scale]))

(def DEFAULT_BEATS 100)

(defn audio-context []
  (cljb/audio-context))

(defn ping [freq]
  (cljb/connect->
    (cljb/square freq)
    (cljb/percussive 0.01 1.4)
    (cljb/gain 0.1)))

(def bass
  (->> (melody/phrase [2 2 4] [0 -1 -2])
       (melody/where :pitch (scale/from -5))))

(defn arrange
  ([notes] (arrange notes {}))
  ([notes {:keys [beats]
           :as options}]
   (let [once (->> notes (melody/with bass))
         beats (or beats DEFAULT_BEATS)]
     (->>
      (melody/cut 4 once)
      (melody/times 2)
      (melody/with (melody/after 8 once))
      (melody/tempo (melody/bpm beats))
      (melody/where :pitch (comp temperament/equal scale/C scale/pentatonic))))))

(defn play
  "Plays a sequence of `notes` in a given `audio-context`.
  Options:
   `register-note!` Callback function called for each note that will be played eventually.
   `beats` Number of beats used to create the melody."
  ([notes audio-context] (play notes audio-context {}))
  ([notes audio-context {:keys [register-note! beats]
                         :as options}]
   (doseq [{:keys [pitch time duration] :as note} (arrange notes {:beats beats})]
     (let [connected (cljb/connect-> (ping pitch) cljb/destination)
           at (+ time (cljb/current-time audio-context))]
       (cljb/run-with connected audio-context at duration)
       (when register-note!
         (register-note! note))))))
