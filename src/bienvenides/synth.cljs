(ns bienvenides.synth
  (:require
   [cljs-bach.synthesis :as cljb]
   [leipzig.temperament :as temperament]
   [leipzig.melody :as melody]
   [leipzig.scale :as scale]))

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

(defn concrete [notes]
  (->> notes
       (melody/tempo (melody/bpm 100))
       (melody/where :pitch (comp temperament/equal scale/C scale/major))))

(defn play [notes audio-context]
  (doseq [{:keys [pitch time duration] :as note} (concrete (melody/with notes bass))]
    (let [connected (cljb/connect-> (ping pitch) cljb/destination)
          at (+ time (cljb/current-time audio-context))]
      (cljb/run-with connected audio-context at duration))))
