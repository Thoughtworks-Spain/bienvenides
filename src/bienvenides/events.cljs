(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.encoding :as encoding]
   [bienvenides.synth :as synth]
   [bienvenides.utils :as utils]
   [leipzig.melody :as melody]))

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
  [(re-frame/inject-cofx :audio-context)]
  (fn [cofx event]
    {:db (merge
          (:db cofx)
          {:audio-context (initialize-audio-context cofx event)})}))

(re-frame/reg-fx
  :play
  (fn [{notes :notes audio-context :audio-context :as all}]
    (synth/play notes audio-context)))

(defn play [cofx [_ name]]
  {:play {:notes (-> name utils/parse-name encoding/encode)
          :audio-context (-> cofx :db :audio-context)}})

(defn new-routing-match
  "Event fired when a new route is matched."
  [{db :db} [_ routing-match]]
  {:db (merge db {:routing-match routing-match})})

(re-frame/reg-event-fx ::play play)
(re-frame/reg-event-fx ::new-routing-match new-routing-match)
