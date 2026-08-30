(ns bløggr.core
  (:require [bløggr.assets :refer [get-assets get-css]]
            [bløggr.index :refer [get-index]]
            [bløggr.posts :refer [get-posts post->path-map post-relative-url]]
            [bløggr.rss :refer [get-rss]]
            [bløggr.settings :refer [settings]]
            [bløggr.sitemap :refer [get-sitemap]]
            [clojure.java.io :as io]
            [optimus.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :as strategies]
            [stasis.core :as stasis])
  (:import [java.time OffsetDateTime]))

(def ^:private export-dir "dist")

(defn- get-pages []
  (stasis/merge-page-sources
    (let [posts (get-posts)
          path-mapped-posts (reduce merge (map post->path-map posts))
          rss (get-rss settings posts)
          sitemap (get-sitemap settings
                               (into {"/" (OffsetDateTime/now)}
                                     (map (juxt post-relative-url #(get-in % [:header :date]))
                                          posts)))
          index (get-index posts)]
      {:posts path-mapped-posts
       :css (get-css)
       :rss {"/rss.xml" rss}
       :index {"/index.html" index}
       :sitemap {"/sitemap.xml" sitemap}})))

(defn- get-pages-reload []
  (require 'bløggr.settings :reload)
  (require 'bløggr.common :reload)
  (require 'bløggr.posts :reload)
  (require 'bløggr.index :reload)
  (require 'bløggr.comments :reload)
  (require 'bløggr.rss :reload)
  (require 'bløggr.sitemap :reload)
  (get-pages))

(defn- sources-signature []
  (transduce (comp (mapcat #(file-seq (io/file %)))
                   (map #(.lastModified ^java.io.File %)))
             max 0 ["posts/" "resources/" "src/" "settings.edn"]))

(def ^:private page-cache (atom {}))

(defn- get-pages-cached []
  (let [sig (sources-signature)]
    (or (@page-cache sig)
        (let [pages (get-pages-reload)]
          (reset! page-cache {sig pages})
          pages))))

(def ring (-> (stasis/serve-pages get-pages-cached)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))

(defn export []
  (let [assets (optimizations/none (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
