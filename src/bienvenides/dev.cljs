(ns bienvenides.dev
  (:require
   [bienvenides.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(dev-setup)
