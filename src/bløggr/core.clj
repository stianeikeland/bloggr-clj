(ns bløggr.core
  (:require [bløggr.assets :refer [get-assets get-css]]
            [bløggr.index :refer [get-index]]
            [bløggr.posts :refer [get-posts post->path-map posts-by-date]]
            [bløggr.rss :refer [get-rss]]
            [optimus.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [stasis.core :as stasis]))

(def export-dir "dist")
(def site-settings (read-string (slurp "settings.edn")))

(defn get-pages []
  (require 'bløggr.posts :reload)
  (stasis/merge-page-sources
   (let [posts (get-posts)
         path-mapped-posts (reduce merge (map post->path-map posts))
         rss (get-rss site-settings posts)
         index (->> posts
                    (posts-by-date)
                    (get-index))]
     {:posts path-mapped-posts
      :css (get-css)
      :rss {"/rss.xml" rss}
      :index {"/index.html" index}})))

(def ring (-> (stasis/serve-pages get-pages)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))

(defn export []
  (let [assets (optimizations/none (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
