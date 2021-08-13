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
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/current-page] root-el)))

(defn register-hash-change-listener!
  "Watches the hash on the url so we can update the re-frame db accordingly"
  []
  (letfn [(on-hashchange [_] (re-frame/dispatch-sync [::events/update-hash js/location.hash]))]
    (js/window.addEventListener "hashchange" on-hashchange)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/update-hash js/location.hash])
  (routing/init!)
  (mount-root)
  (register-hash-change-listener!))
