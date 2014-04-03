(ns bløggr.core
  (:require [bløggr.common :refer :all]
            [bløggr.posts :refer :all]
            [bløggr.assets :refer :all]
            [stasis.core :as stasis]
            [optimus.prime :as optimus]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :as strategies]))

(defn get-page-sources []
  (hash-map :posts (get-posts)
            :css (get-css)))

(def ring (-> (get-page-sources)
              (stasis/merge-page-sources)
              (stasis/serve-pages)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))
