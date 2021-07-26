(ns bienvenides.events
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.db :as db]
   [clojure.string :as str]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    (let [raw (if-let [hash js/window.location.hash]
                (subs hash 1)
                "")
          name (str/split raw #"%20")]
      (-> db/default-db
          (assoc :name name)))))

(re-frame/reg-event-db
  ::play
  (fn [_ _]
    (js/console.log "Played!")))
