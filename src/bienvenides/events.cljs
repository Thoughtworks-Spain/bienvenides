(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [bienvenides.encoding :as encoding]
   [bienvenides.synth :as synth]
   [bienvenides.utils :as utils]
   [leipzig.melody :as melody]))

(re-frame/reg-cofx
   :hash-fragment
   (fn [cofx _]
      (let [raw js/window.location.hash
            processed (if (empty? raw) "#Anon" raw)]
        (-> cofx
          (assoc :hash-fragment processed)))))

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
          (:db cofx)
          {:name (-> cofx :hash-fragment utils/hash->name)}
          {:audio-context (initialize-audio-context cofx event)})}))

(re-frame/reg-fx
  :play
  (fn [{notes :notes audio-context :audio-context :as all}]
    (synth/play notes audio-context)))

(defn play [cofx event]
  {:play {:notes (-> cofx :db :name encoding/encode)
          :audio-context (-> cofx :db :audio-context)}})

(defn update-hash [{db :db} [_ val]]
  (let [parsed-hash (if (empty? val) "#Anon" val)]
    {:db (merge
          db
          {:hash parsed-hash})}))

(re-frame/reg-event-fx ::update-hash update-hash)
(re-frame/reg-event-fx ::play play)
