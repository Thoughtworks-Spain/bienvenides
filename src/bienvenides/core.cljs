(ns bienvenides.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.views :as views]

   [bienvenides.routing :as routing]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [views/current-page] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (routing/init!)
  (mount-root))
