(ns bienvenides.views
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.subs :as subs]
   ))

(defn not-found []
  [:h1 "Ups, I don't exist :("])

(defn main-panel-core [{:keys [name]}]
  [:div
   [:h1 "Bienvenides " name]
   [:button {:on-click #(re-frame/dispatch [::events/play name])} "Play"]])

(defn main-panel [props]
  "The main app entrypoint, which gives a warm welcome to the user :)"
  [main-panel-core {:name (or (some-> props :routing-match :query-params :name)
                              "Anom")}])

(defn url-generator
  "A view that allows the user to generate a custom url for a specific name."
  []
  [:div
   [:h1 "Url Generator"]
   [:div [:input]]])

(defn current-page-core [{:keys [routing-match]}]
  (let [view (or (some-> routing-match :data :view) not-found)]
    [view {:routing-match routing-match}]))

(defn current-page
  "Renders the current page depending on the routing match."
  []
  [current-page-core {:routing-match @(re-frame/subscribe [::subs/routing-match])}])
