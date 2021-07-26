(ns bienvenides.synth
  (:require
   [cljs-bach.synthesis :as cljb]))

(defn ping [freq]
  (cljb/connect->
    (cljb/square freq)
    (cljb/percussive 0.01 0.4)
    (cljb/gain 0.1)))

(def audio-context cljb/audio-context)
