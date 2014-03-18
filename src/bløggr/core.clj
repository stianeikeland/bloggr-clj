(ns bløggr.core
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [me.raynes.cegdown :as md2]))

(defn get-posts []
  (stasis/slurp-directory "resources/posts/" #".*\.(md|markdown)$"))

(defn markdown [pages]
  (zipmap (map #(str/replace % #"\.(md|markdown)$" ".html" ) (keys pages))
          (map md2/to-html (vals pages))))

(def ring (-> (get-posts)
              markdown
              stasis/serve-pages))
