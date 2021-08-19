(ns bienvenides.build.hooks
  "Provide shadow-cljs hooks that are used during the build process, both for development and release."
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as string]
            [clojure.core.async :as async]))


;;
;; Globals
;; 
(def INDEX_TEMPLATE "./src/index.template.html")
(def STATIC_FILES ["fonts"])
(defonce sass-running? (atom false))


;; 
;; Helpers
;; 
(defn call-cmd!
  "Helper fn that calls a function via shell/sh"
  [cmd]
  (->> cmd (string/join " ") (str "Calling command: ") println)
  (let [{:keys [exit out err] :as sh-result} (apply shell/sh cmd)]
    (when (not= exit 0)
      (throw (ex-info "Failed to call cmd" {:sh-result sh-result})))))

(defn run-process!
  "Helper fn that runs a new child process via ProcessBuilder, using the same io of the current process."
  [cmd]
  (->> cmd (string/join " ") (str "Starting process: ") println)
  (-> cmd ProcessBuilder. .inheritIO .start .waitFor)
  (->> cmd (string/join " ") (str "Process exited: ") println))


;;
;; Hooks
;; 
(defn generate-index
  "Generates the `index.html` file at the target directory with the correct entrypoint and stylesheet urls"
  {:shadow.build/stage :flush}
  [build-state & _]
  (let [entrypoint-url (-> build-state :shadow.build/config :asset-path (str "/app.js"))
        stylesheet-url (-> build-state :shadow.build/config :bienvenides.build-config/stylesheet-url)
        target-dir (-> build-state :shadow.build/config :bienvenides.build-config/target-dir)
        dest-file (str target-dir "/index.html")
        process-line (fn [line]
                       (-> line
                           (string/replace "{{ENTRYPOINT_URL}}" entrypoint-url)
                           (string/replace "{{STYLESHEET_URL}}" stylesheet-url)
                           (str "\n")))]
    (println (str "Writting file " dest-file " with entrypoint " entrypoint-url " and stylesheet " stylesheet-url))
    (io/make-parents dest-file)
    (with-open [reader (io/reader INDEX_TEMPLATE)]
      (with-open [writer (io/writer dest-file)]
        (doseq [line (line-seq reader)]
          (->> line process-line (.write writer)))))
    build-state))

(defn compile-scss
  "Compiles all scss. Only used for production"
  {:shadow.build/stage :flush}
  [build-state & _]
  (let [target-dir (-> build-state :shadow.build/config :bienvenides.build-config/target-dir)
        target-file (str target-dir "/css/compiled/index.css")
        cmd ["npx" "sass" "./src/scss/core.scss" target-file]]
    (call-cmd! cmd)
    build-state))

(defn copy-static-files
  "Copies all static files to the target directory. Used only for production."
  {:shadow.build/stage :flush}
  [build-state & _]
  (let [target-dir (-> build-state :shadow.build/config :bienvenides.build-config/target-dir)]
    (doseq [static-file STATIC_FILES
            :let [from (str "./resources/public/" static-file)
                  to (str target-dir "/" static-file)
                  cmd ["cp" "-r" from to]]]
      (call-cmd! cmd)))
  build-state)

(defn watch-scss
  "Compiles scss, trigering recompilation on changes. Only used for
  dev. Notice that by relying on `:configure` stage this hook only run
  once during `shadow-cljs watch`.."
  {:shadow.build/stage :configure}
  [build-state & _]
  (let [target-dir (-> build-state :shadow.build/config :bienvenides.build-config/target-dir)
        cmd ["npx" "sass" "--watch" "./src/scss/core.scss" (str target-dir "/css/compiled/index.css")]
        on-exit (fn [_]
                  (println "WARNING: SASS STOPPED")
                  (reset! sass-running? false))]
    (if-not @sass-running?
      (async/go
        (reset! sass-running? true)
        (run-process! cmd)
        (reset! sass-running? false))
      (println "Skipping scss watch because it's already running"))
    build-state))
