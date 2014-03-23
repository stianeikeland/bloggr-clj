(ns bløggr.core
  (:require [bløggr.common :refer :all]
            [bløggr.posts :refer :all]
            [stasis.core :as stasis]
            [clojure.string :as str]
            [clj-time.format :as tf]
            [clj-time.core :as t]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]
            [clojure.java.io :as io]
            [optimus.prime :as optimus]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :as strategies]))

(defn get-css []
  (stasis/slurp-directory "resources/css/" #".*\.css"))

(defn get-page-sources []
  (hash-map :posts (get-posts)
            :css (get-css)))

(defn load-assets [path]
  (assets/load-assets path [#".*"]))

(defn get-assets []
  (reduce merge (map #(assoc % :path (str "/images" (% :path))) (load-assets "images"))
                (map #(assoc % :path (str "/fonts" (% :path))) (load-assets "fonts"))))

(def ring (-> (get-page-sources)
              (stasis/merge-page-sources)
              (stasis/serve-pages)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))
