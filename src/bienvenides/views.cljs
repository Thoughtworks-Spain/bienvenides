(ns bienvenides.views
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Bienvenides " (@name 0)]
     ]))
