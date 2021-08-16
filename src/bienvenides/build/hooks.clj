(ns bienvenides.build.hooks
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :as pprint]
            [clojure.string :as string]))

(def INDEX_TEMPLATE "./src/bienvenides/index.template.html")

(defn generate-index
  "Generates an `index.html` file at the public folder with the correct entrypoint url based on a template"
  {:shadow.build/stage :flush}
  [build-state entrypoint-url destination-folder]
  (let [dest-file (str destination-folder "/index.html")]
    (prn (str "Writting file " dest-file " with entrypoint " entrypoint-url))
    (with-open [reader (io/reader INDEX_TEMPLATE)]
      (with-open [writer (io/writer dest-file)]
        (doseq [line (line-seq reader)]
          (.write writer (string/replace line "{{ENTRYPOINT_URL}}" entrypoint-url))
          (.write writer "\n"))))
    build-state))
