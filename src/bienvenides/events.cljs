(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [clojure.string :as str]
   [bienvenides.synth :as synth]
   [leipzig.melody :as melody]))

(defn initialize-name [cofx event]
  (let [raw (if-let [hash (:hash-fragment cofx)]
              (subs hash 1)
              "")]
    (->> (str/split raw #"%20")
         (filter (partial not= "")))))

(re-frame/reg-cofx
   :hash-fragment
   (fn [cofx _]
      (-> cofx
          (assoc :hash-fragment js/window.location.hash))))

(defn initialize-audio-context [cofx event]
  (let [audio-context (:audio-context cofx)]
    audio-context))

(re-frame/reg-cofx
  :audio-context
  (fn [cofx _]
    (-> cofx
        (assoc :audio-context (synth/audio-context)))))

(re-frame/reg-event-fx
  ::initialize-db
  [(re-frame/inject-cofx :hash-fragment)
   (re-frame/inject-cofx :audio-context)]
  (fn [cofx event]
    {:db (merge
           {:name (initialize-name cofx event)}
           {:audio-context (initialize-audio-context cofx event)})}))

(re-frame/reg-fx
  :play
  (fn [{notes :notes audio-context :audio-context}]
    (synth/play notes audio-context)))

(defn play [cofx event]
  {:play {:notes (melody/phrase [1] [0])
          :audio-context (-> cofx :db :audio-context)}})

(re-frame/reg-event-fx
  ::play
  play)
