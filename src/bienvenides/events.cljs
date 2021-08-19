(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.encoding :as encoding]
   [bienvenides.synth :as synth]
   [bienvenides.utils :as utils]
   [leipzig.melody :as melody]
   [clojure.core.async :as async]))

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

(defn play-fx
  [{:keys [notes audio-context play-options]}]
  (letfn [(register-note! [{:keys [time duration] :as note}]
            (async/go
              (async/<! (utils/timeout-seconds time))
              (re-frame/dispatch [::note-started-playing note])
              (async/<! (utils/timeout-seconds duration))
              (re-frame/dispatch [::note-stopped-playing note])))]
    (let [play-options' (merge play-options {:register-note! register-note!})]
      (synth/play notes audio-context play-options'))))

(re-frame/reg-fx :play play-fx)

(defn play [cofx [_ names]]
  {:play {:notes (encoding/encode names)
          :audio-context (-> cofx :db :audio-context)
          :play-options (some-> cofx :db :play-options)}})

(defn new-routing-match
  "Event fired when a new route is matched."
  [{db :db} [_ routing-match]]
  {:db (merge db {:routing-match routing-match})})

(defn note-started-playing
  "Event fired when a note starts playing"
  [{{:keys [current-notes] :as db} :db} [_ note]]
  (let [new-notes (conj (or current-notes #{}) note)]
    {:db (merge db {:current-notes new-notes})}))

(defn note-stopped-playing
  "Event fired when a note stops playing"
  [{{:keys [current-notes] :as db} :db} [_ note]]
  (let [new-notes (disj (or current-notes #{}) note)]
    {:db (merge db {:current-notes new-notes})}))

(re-frame/reg-event-fx ::play play)
(re-frame/reg-event-fx ::new-routing-match new-routing-match)
(re-frame/reg-event-fx ::note-started-playing note-started-playing)
(re-frame/reg-event-fx ::note-stopped-playing note-stopped-playing)
