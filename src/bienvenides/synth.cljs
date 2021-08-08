(ns bienvenides.synth
  (:require
   [cljs-bach.synthesis :as cljb]))

(defn audio-context []
  (cljb/audio-context))

(defn ping [freq]
  (cljb/connect->
    (cljb/square freq)
    (cljb/percussive 0.01 0.4)
    (cljb/gain 0.1)))

(defn play [notes audio-context]
  (-> (ping 440)
      (cljb/connect-> cljb/destination)
      (cljb/run-with audio-context (cljb/current-time audio-context) 1.0)))
