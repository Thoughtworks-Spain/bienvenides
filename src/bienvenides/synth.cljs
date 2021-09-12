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
    (cljb/triangle freq)
    (cljb/percussive 0.005 1.2)
    (cljb/gain 0.1)))

(defn pong [freq]
  (cljb/connect->
    (cljb/triangle freq)
    (cljb/adshr 0.01 0.2 0.4 0.8 0.3)
    (cljb/gain 0.05)))

(def bass
  (->> (melody/phrase [2 2 4] [0 -1 -2])
       (melody/where :pitch (scale/from -5))
       (melody/all :part :bass)))

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
   (doseq [{:keys [pitch time duration part] :as note} (arrange notes {:beats beats})]
     (let [connected (cljb/connect-> (ping pitch) cljb/destination)
           connected-bass (cljb/connect-> (pong pitch) cljb/destination)
           at (+ time (cljb/current-time audio-context))]
       (if (= part :bass)
         (cljb/run-with connected-bass audio-context at duration)
         (cljb/run-with connected audio-context at duration))
       (when register-note!
         (register-note! note))))))
