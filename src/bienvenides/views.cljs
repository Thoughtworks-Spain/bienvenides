(ns bienvenides.views
  (:require
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.subs :as subs]
   [bienvenides.utils :as utils]
   [reagent.core :as r]))

(defn not-found []
  [:h1 "Ups, I don't exist :("])

(defn main-panel-core [{:keys [name]}]
  [:div.main-panel
   [:h1 "Bienvenides " name]
   [:button.button {:on-click #(re-frame/dispatch [::events/play name])} "Play"]])

(defn main-panel [props]
  "The main app entrypoint, which gives a warm welcome to the user :)"
  [main-panel-core {:name (or (some-> props :routing-match :query-params :name)
                              "Anom")}])

(defn url-generator-core
  [{:keys [value on-change]}]
  (let [url (utils/generate-url @value)]
    [:div
     [:h1 "Url Generator"]
     [:form {:on-submit (fn [e]
                          (.preventDefault e)
                          (.then (js/navigator.clipboard.writeText url)
                                 #(js/alert "Copied to clipboard!")))}
      [:div
       [:span "Name:"]
       [:input {:value @value
                :on-change #(-> % .-target .-value on-change)
                :auto-focus true}]
       [:div "Generated url: " [:a {:href url} url]]]
      [:input {:type "submit"
               :value "Copy!"}]]]))

(defn url-generator
  "A view that allows the user to generate a custom url for a specific name."
  []
  (let [value (r/atom "")]
    (fn []
      [url-generator-core {:value value
                           :on-change #(reset! value %)}])))

(defn current-page-core [{:keys [routing-match]}]
  (let [view (or (some-> routing-match :data :view) not-found)]
    [view {:routing-match routing-match}]))

(defn current-page
  "Renders the current page depending on the routing match."
  []
  [current-page-core {:routing-match @(re-frame/subscribe [::subs/routing-match])}])