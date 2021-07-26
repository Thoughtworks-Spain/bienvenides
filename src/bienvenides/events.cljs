(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [clojure.string :as str]))

(defn initialize-db [cofx event]
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

(re-frame/reg-event-fx
  ::initialize-db
  [(re-frame/inject-cofx :hash-fragment)]
  (fn [cofx event] {:db (initialize-db cofx event)}))

(re-frame/reg-fx
  :log
  (fn [value]
    (js/console.log value)))

(defn play [_ _]
  {:log "Played!"})

(re-frame/reg-event-fx
  ::play
  play)
