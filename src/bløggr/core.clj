(ns bløggr.core
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [me.raynes.cegdown :as md]
            [clj-time.format :as tf]
            [clj-time.core :as t]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]))

(def cegdown-ext [:fenced-code-blocks :autolinks])

(defn get-posts []
  (stasis/slurp-directory "posts/" #"test.*\.(md|markdown)$"))

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
       title "/index.html"))

(defn filename-body-map [post]
  {(filename (get-in post [:header :slug])
             (get-in post [:header :date])) (post :body)})

(defn markdown [post]
  (assoc post :body (md/to-html (post :body) cegdown-ext)))

(defn- highlight-node [n]
  (let [lang (:class (:attrs n))]
    (if (nil? lang) n
        (assoc n :content (html/html-snippet (pygments/highlight (apply str (:content n))
                                                                 (keyword lang)
                                                                 :html))))))
(defn highlight [post]
  (assoc post :body (html/at (post :body) [:pre :code] highlight-node)))

(defn enliveify [post]
  (assoc post :body (html/html-snippet (post :body))))

(defn render [post]
  (assoc post :body (apply str (html/emit* (post :body)))))

(def ring (stasis/serve-pages {"/index.html" (->> (get-posts)
                                                  (vals)
                                                  (map parse-post)
                                                  (map markdown)
                                                  (map enliveify)
                                                  (map highlight)
                                                  (map render)
                                                  (first)
                                                  (:body))}))


;(def ring (->> (get-posts)
;               (vals)
;               (map parse-post)
;               (map markdown)
;               (reduce merge)
;               (stasis/serve-pages)))

