(ns bienvenides.views
  (:require
   [clojure.string :as string]
   [re-frame.core :as re-frame]
   [bienvenides.events :as events]
   [bienvenides.subs :as subs]
   [bienvenides.utils :as utils]
   [reagent.core :as r]))

(def letter-class "main-panel__letter")
(def letter-class-active (str letter-class " " "main-panel__letter--active"))

(defn not-found []
  [:h1 "Ups, I don't exist :("])

(defn main-panel-name
  "A component that renders the name of the person inside the main panel."
  [{:keys [names current-notes]}]
  (letfn [(letter-playing? [name-index letter-index]
            (some #(and (= name-index (:bienvenides/name-index %))
                        (= letter-index (:bienvenides/letter-index %)))
                  current-notes))
          (render-letter [name-index letter-index letter]
            (let [key (str name-index letter-index letter)
                  class (if (letter-playing? name-index letter-index) letter-class-active letter-class)]
              [:span {:key key :class class} letter]))]
    [:span.main-panel__full-name
     (for [[name-index name] (map vector (range) names)]
       [:span.main-panel__single-name {:key name-index}
        (for [[letter-index letter] (map vector (range) name)]
          (render-letter name-index letter-index letter))])]))

(defn main-panel-bpm-input
  "An input to control the speed of the song."
  [{:keys [play-options]}]
  (letfn [(on-change [event]
            (let [beats (-> event .-target .-value)
                  play-options' (merge play-options {:beats beats})]
              (re-frame/dispatch [::events/set-play-options play-options'])))]
    [:input {:type "number"
             :min 10 :max 999     ; For browsers that recognise type number.
             :size 3 :maxlength 3 ; For browsers that don't recognise type number.
             :on-change on-change
             :value (:beats play-options)}]))

(defn main-panel-core [{:keys [names current-notes play-options]}]
  [:div.main-panel
   [:h1 "Bienvenides "
    [main-panel-name {:names names :current-notes current-notes}]]
   [:div.main-panel__control-dashboard
    [:span.main-panel__dashboard-input-wrapper
     [:label "Speed:"
      [main-panel-bpm-input {:play-options play-options}]]]]
   [:button.button {:on-click #(re-frame/dispatch [::events/play names])
                    :disabled (not (empty? current-notes))} "Play"]])

(defn main-panel [props]
  "The main app entrypoint, which gives a warm welcome to the user :)"
  [main-panel-core {:names (or (some-> props :routing-match :query-params :name utils/parse-names)
                               ["Anon"])
                    :current-notes @(re-frame/subscribe [::subs/current-notes])
                    :play-options @(re-frame/subscribe [::subs/play-options])}])

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
