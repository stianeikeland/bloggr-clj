(ns bløggr.core
  (:require [bløggr.common :refer :all]
            [bløggr.posts :refer :all]
            [bløggr.index :refer :all]
            [bløggr.assets :refer :all]
            [bløggr.rss :refer :all]
            [stasis.core :as stasis]
            [optimus.prime :as optimus]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :as strategies]
            [optimus.export]))

(def export-dir "dist")
(def site-settings (read-string (slurp "settings.edn")))

(defn get-page-sources []
  (stasis/merge-page-sources
   (let [posts (get-posts)]
     (hash-map :css (get-css)
               :posts (reduce merge (map filename-body-map posts))
               :index {"/index.html" (->> posts
                                          (posts-by-date)
                                          (get-index))}
               :rss {"/rss.xml" (get-rss site-settings posts)}))))

(def ring (-> (get-page-sources)
              (stasis/serve-pages)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-page-sources) export-dir {:optimus-assets assets})))
