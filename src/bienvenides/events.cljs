(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [clojure.string :as str]
   [bienvenides.synth :as synth]))

(defn initialize-name [cofx event]
    (let [raw (if-let [hash (:hash-fragment cofx)]
                (subs hash 1)
                "")
          name (->> (str/split raw #"%20")
                    (filter (partial not= "")))]
      (-> db/default-db
          (assoc :name name))))

(re-frame/reg-cofx
   :hash-fragment
   (fn [coeffects _]
      (assoc coeffects :hash-fragment js/window.location.hash)))

(defn initialize-audio-context [cofx event]
    (let [audio-context (:audio-context cofx)]
      (-> {:audio-context audio-context})))

(re-frame/reg-fx
  :audio-context
  synth/audio-context)

(re-frame/reg-event-fx
  ::initialize-db
  [(re-frame/inject-cofx :hash-fragment)
   (re-frame/inject-cofx :audio-context)]
  (fn [cofx event]
    {:db (merge
           (initialize-name cofx event)
           (initialize-audio-context cofx event))}))

(re-frame/reg-fx
  :log
  (fn [value]
    (js/console.log value)))

(defn play [_ _]
  {:log "Played!"})

(re-frame/reg-event-fx
  ::play
  play)
