(ns bienvenides.subs
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.utils :as utils]))

(def DEFAULT_PLAY_OPTIONS {:beats 100})
(def DEFAULT_ENCODING_OPTIONS {:duration {:vowel 1 :consonant 0.5}})

(re-frame/reg-sub
 ::routing-match
 (fn [db]
   (some-> db :routing-match)))

(re-frame/reg-sub
 ::current-notes
 (fn [db]
   (or (some-> db :current-notes) #{})))

(re-frame/reg-sub
 ::play-options
 (fn [db]
   (or (some-> db :play-options)
       DEFAULT_PLAY_OPTIONS)))

(re-frame/reg-sub
 ::encoding-options
 (fn [db]
   (or (some-> db :encoding-options)
       DEFAULT_ENCODING_OPTIONS)))
