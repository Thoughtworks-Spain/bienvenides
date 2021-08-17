(ns bienvenides.build.hooks
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clojure.core.async :as async]))

(def INDEX_TEMPLATE "./src/bienvenides/index.template.html")
(defonce sass-running? (atom false))

(defn generate-index
  "Generates an `index.html` file at the public folder with the correct entrypoint url based on a template"
  {:shadow.build/stage :flush}
  [build-state & _]
  (let [entrypoint-url (-> build-state :shadow.build/config :asset-path (str "/app.js"))
        stylesheet-url (-> build-state :shadow.build/config :bienvenides/stylesheet-url)
        target-dir (-> build-state :shadow.build/config :bienvenides/target-dir)
        dest-file (str target-dir "/index.html")]
    (prn (str "Writting file " dest-file " with entrypoint " entrypoint-url))
    (io/make-parents dest-file)
    (with-open [reader (io/reader INDEX_TEMPLATE)]
      (with-open [writer (io/writer dest-file)]
        (doseq [line (line-seq reader)]
          (.write writer (-> line
                             (string/replace "{{ENTRYPOINT_URL}}" entrypoint-url)
                             (string/replace "{{STYLESHEET_URL}}" stylesheet-url)))
          (.write writer "\n"))))
    build-state))

(defn compile-scss
  "Compiles all scss. Only used for production"
  {:shadow.build/stage :flush}
  [build-state & _]
  (let [target-dir (-> build-state :shadow.build/config :bienvenides/target-dir)
        cmd ["npx" "sass" "./src/scss/core.scss" (str target-dir "/css/compiled/index.css")]]
    (->> cmd (string/join " ") (str "Calling command: ") prn)
    (let [{:keys [exit out err] :as sh-result} (apply shell/sh cmd)]
      (when (not= exit 0)
        (throw (ex-info "Failed to compile scss to css" {:sh-result sh-result}))))
    build-state))

(defn watch-scss
  "Compiles scss, trigering recompilation on changes. Only used for
  dev. Notice that by relying on `:configure` stage this hook only run
  once during `shadow-cljs watch`.."
  {:shadow.build/stage :configure}
  [build-state & _]
  (let [target-dir (-> build-state :shadow.build/config :bienvenides/target-dir)
        cmd ["npx" "sass" "--watch" "./src/scss/core.scss" (str target-dir "/css/compiled/index.css")]
        on-exit (fn [_]
                  (prn "WARNING: SASS STOPPED")
                  (reset! sass-running? false))]
    (when-not @sass-running?
      (async/go
        (reset! sass-running? true)
        (->> cmd (string/join " ") (str "Calling command: ") prn)
        (-> cmd ProcessBuilder. .inheritIO .start .waitFor)
        (prn "WARNING: SASS WATCH STOPPED!")
        (reset! sass-running? false)))
    build-state))
