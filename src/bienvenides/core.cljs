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

(defn ensure-hash! []
  (when (= js/window.location.hash "")
    (set! js/window.location.hash "#/")))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (routing/init!)
  (ensure-hash!)
  (mount-root))
