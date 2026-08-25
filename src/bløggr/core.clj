(ns bløggr.core
  (:require [bløggr.assets :refer [get-assets get-css]]
            [bløggr.index :refer [get-index]]
            [bløggr.posts :refer [get-posts post->path-map posts-by-date]]
            [bløggr.rss :refer [get-rss]]
            [bløggr.sitemap :refer [get-sitemap]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [optimus.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [stasis.core :as stasis]))

(def export-dir "dist")
(def site-settings (edn/read-string (slurp "settings.edn")))

(defn get-pages []
  (stasis/merge-page-sources
   (let [posts (get-posts)
         path-mapped-posts (reduce merge (map post->path-map posts))
         rss (get-rss site-settings posts)
         sitemap (get-sitemap site-settings (cons "/" (keys path-mapped-posts)))
         index (->> posts
                    (posts-by-date)
                    (get-index))]
     {:posts path-mapped-posts
      :css (get-css)
      :rss {"/rss.xml" rss}
      :index {"/index.html" index}
      :sitemap {"/sitemap.xml" sitemap}})))

(defn get-pages-reload []
  (require 'bløggr.posts :reload)
  (require 'bløggr.index :reload)
  (get-pages))

(defn- sources-signature []
  (transduce (comp (mapcat #(file-seq (io/file %)))
                   (map #(.lastModified ^java.io.File %)))
             max 0 ["posts/" "resources/" "src/" "settings.edn"]))

(def page-cache (atom {}))

(defn get-pages-cached []
  (let [sig (sources-signature)]
    (or (get @page-cache sig)
        (get (reset! page-cache {sig (get-pages-reload)}) sig))))

(def ring (-> (stasis/serve-pages get-pages-cached)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))

(defn export []
  (let [assets (optimizations/none (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
