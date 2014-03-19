(ns bløggr.core
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [me.raynes.cegdown :as md2]))


(defn parse-post [post]
  (let [x (str/split post #"\n------\n")]
    {:body (second x)
     :header (read-string (first x))}))

(defn get-posts []
  (stasis/slurp-directory "resources/posts/" #"2013-07-25.*\.(md|markdown)$"))

(defn markdown [pages]
  (zipmap (map #(str/replace % #"\.(md|markdown)$" ".html" ) (keys pages))
          (map md2/to-html (vals pages))))

(def ring (-> (get-posts)
              markdown
              stasis/serve-pages))

