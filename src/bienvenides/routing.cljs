(ns bienvenides.routing
  (:require
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.views :as views]))

(def routes
  "All routes available in the app, used by reitit to match."
  [["/"
    {:name "Main Panel"
     :view views/main-panel}]

   ["/url-generator"
    {:name "Url Generator"
     :view views/url-generator}]])

(defn init!
  "Initializes the app's routing using reitit-frontend."
  []
  (rfe/start!
   (rf/router routes)
   (fn [m] (re-frame/dispatch [::events/new-routing-match m]))
   {:use-fragment true}))
