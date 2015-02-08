(ns bløggr.assets
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [optimus.assets :as assets]))

(defn get-css []
  (stasis/slurp-directory "resources/css/" #".*\.css"))

(defn load-assets [path]
  (assets/load-assets path [#".*"]))

(defn get-assets []
  (concat (map #(assoc % :path (str "/js" (% :path))) (load-assets "js"))
          (map #(assoc % :path (str "/images" (% :path))) (load-assets "images"))
          (map #(assoc % :path (str "/fonts" (% :path))) (load-assets "fonts"))))
