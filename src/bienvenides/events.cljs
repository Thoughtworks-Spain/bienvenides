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
      (assoc coeffects :hash-fragment (js/window.location.hash))))

(re-frame/reg-event-db
  ::initialize-db
  [re-frame/inject-cofx]
  initialize-db)

(re-frame/reg-event-db
  ::play
  (fn [_ _]
    (js/console.log "Played!")))
