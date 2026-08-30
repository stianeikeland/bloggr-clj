(ns bløggr.comments
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private comments-by-url
  (edn/read-string (slurp (io/file "resources/comments.edn"))))

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
  (let [comments (get comments-by-url url)
        n (count-tree comments)]
    (when (pos? n)
      (format "<section id=\"comments\"><h2>%s</h2><ol>%s</ol></section>"
              (if (= 1 n) "1 comment" (str n " comments"))
              (apply str (map render-comment comments))))))
