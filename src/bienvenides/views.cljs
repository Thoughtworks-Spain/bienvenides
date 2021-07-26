(ns bienvenides.views
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        first-name (first @name)]
    [:div
     [:h1 "Bienvenides " first-name]
     [:button {:on-click #(re-frame/dispatch [::events/play])} "Play"]]))
