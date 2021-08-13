(ns bienvenides.build.hooks
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.pprint :as pprint]
            [clojure.string :as string]))

(defn generate-index
  "Generates an `index.html` file at the public folder with the correct entrypoint url"
  {:shadow.build/stage :flush}
  [build-state entrypoint-url destination-folder]
  (let [dest-file (str destination-folder "/index.html")]
    (prn (str "Writting file " dest-file " with entrypoint " entrypoint-url))
    (with-open [reader (io/reader "./src/bienvenides/index.template.html")]
      (with-open [writer (io/writer dest-file)]
        (doseq [line (line-seq reader)]
          (.write writer (string/replace line "{{ENTRYPOINT_URL}}" entrypoint-url))
          (.write writer "\n"))))
    build-state))
