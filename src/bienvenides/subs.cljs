(ns bienvenides.subs
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.utils :as utils]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (-> db :hash utils/hash->name)))

(re-frame/reg-sub
 ::routing-match
 (fn [db]
   (some-> db :routing-match)))
