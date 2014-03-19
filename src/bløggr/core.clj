(ns bløggr.core
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [me.raynes.cegdown :as md]
            [clj-time.format :as tf]
            [clj-time.core :as t]))


(defn get-posts []
  (stasis/slurp-directory "resources/posts/" #"2013-07-25.*\.(md|markdown)$"))


(defn parse-datestring [date-str]
  (tf/parse (tf/formatter "yyyy-MM-dd HH:mm:ssZ") date-str))

(defn parse-post
  "Parse a blog post into header map and body string. Convert string date to DateTime"
  [post]
  (let [x (str/split post #"\n------\n")]
       {:body (second x)
       :header (let [header (read-string (first x))]
                    (assoc header :date (parse-datestring (header :date))))}))


(defn filename [title date]
  (str (tf/unparse (tf/formatter "/yyyy/MM/dd/") date)
       title
       "/index.html"))

(defn filename-body-map [post]
  {(filename (get-in post [:header :slug])
             (get-in post [:header :date]))
   (post :body)})


(defn markdown [post]
  (assoc post :body (md/to-html (post :body))))


(def ring (->> (get-posts)
               (vals)
               (map parse-post)
               (map markdown)
               (map filename-body-map)
               (reduce merge)
               (stasis/serve-pages)))

