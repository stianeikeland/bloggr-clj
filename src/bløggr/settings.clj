(ns bløggr.settings
  (:require [clojure.edn :as edn]))

(def settings (edn/read-string (slurp "settings.edn")))
