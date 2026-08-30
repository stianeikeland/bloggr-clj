(ns bløggr.assets
  (:require [bløggr.pygmentize :as pygmentize]
            [stasis.core :as stasis]
            [optimus.assets :as assets]
            [optimus.digest :as digest]))

(defn get-css []
  (merge (stasis/slurp-directory "resources/css/" #".*\.css")
         {"/pygments.css" (pygmentize/stylesheet)}))

(defn css-fingerprints
  "Map of css path -> sha-1 fingerprint of its content, used for cache-busting urls."
  []
  (->> (get-css)
       (map (fn [[path content]] [path (digest/sha-1 content)]))
       (into {})))

(defn load-assets [path]
  (assets/load-assets path [#".*"]))

(defn get-assets []
  (concat (map #(assoc % :path (str "/js" (% :path))) (load-assets "js"))
          (map #(assoc % :path (str "/images" (% :path))) (load-assets "images"))
          (map #(assoc % :path (str "/fonts" (% :path))) (load-assets "fonts"))))
