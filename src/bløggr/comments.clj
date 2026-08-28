(ns bløggr.comments
  (:require [bløggr.common :refer [cached-slurp]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; Bodies are pre-sanitized by bin/disqus-to-edn.py (whitelist: p, br,
;; a[href], code, pre) and emitted as trusted HTML; names/dates are escaped here.
(def parsed-cache (atom {:mtime 0 :data {}}))

(defn- comments-by-url []
  (let [mtime (.lastModified (io/file "resources/comments.edn"))]
    (if (= mtime (:mtime @parsed-cache))
      (:data @parsed-cache)
      (let [data (edn/read-string (cached-slurp "resources/comments.edn"))]
        (reset! parsed-cache {:mtime mtime :data data})
        data))))

(defn- esc [s]
  (-> s
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- count-tree [comments]
  (reduce #(+ %1 1 (count-tree (:children %2))) 0 comments))

(defn- render-comment [{:keys [name created body children]}]
  (str "<li><span class=\"comment-author\">" (esc name) "</span>"
    " <time datetime=\"" (esc (str/replace created " " "T")) "\">" (esc created) "</time>"
    "<div class=\"comment-body\">" body "</div>"
    (when (seq children)
      (str "<ol>" (apply str (map render-comment children)) "</ol>"))
    "</li>"))

(defn comments-html
  "Static comment section for a post url (/yyyy/mm/dd/slug/), or nil."
  [url]
  (let [comments (get (comments-by-url) url)
        n (count-tree comments)]
    (when (pos? n)
      (format "<section id=\"comments\"><h2>%s</h2><ol>%s</ol></section>"
              (if (= 1 n) "1 comment" (str n " comments"))
              (apply str (map render-comment comments))))))
