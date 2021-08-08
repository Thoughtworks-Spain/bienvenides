(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [bienvenides.encoding :as encoding]
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
           {:name (initialize-name cofx event)}
           {:audio-context (initialize-audio-context cofx event)})}))

(re-frame/reg-fx
  :play
  (fn [{notes :notes audio-context :audio-context :as all}]
    (synth/play notes audio-context)))

(defn play [cofx event]
  {:play {:notes (-> cofx :db :name encoding/encode)
          :audio-context (-> cofx :db :audio-context)}})

(re-frame/reg-event-fx
  ::play
  play)
