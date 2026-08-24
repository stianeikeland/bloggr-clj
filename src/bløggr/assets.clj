(ns bløggr.assets
  (:require [bløggr.pygmentize :as pygmentize]
            [stasis.core :as stasis]
            [clojure.string :as str]
            [optimus.assets :as assets]))

(defn get-css []
  (merge (stasis/slurp-directory "resources/css/" #".*\.css")
         {"/pygments.css" (pygmentize/stylesheet)}))

(defn load-assets [path]
  (assets/load-assets path [#".*"]))

(defn get-assets []
  (concat (map #(assoc % :path (str "/js" (% :path))) (load-assets "js"))
          (map #(assoc % :path (str "/images" (% :path))) (load-assets "images"))
          (map #(assoc % :path (str "/fonts" (% :path))) (load-assets "fonts"))))
