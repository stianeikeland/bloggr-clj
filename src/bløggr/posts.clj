(ns bløggr.posts
  (:require [bløggr.common :refer :all]
            [stasis.core :as stasis]
            [clojure.string :as str]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]))

(def lead-length 500)

(html/deftemplate post-template "layouts/post.html" [header body]
                  [:head] (html/html-content (slurp "resources/partials/head.html"))
                  [:div#scripts] (html/html-content (slurp "resources/partials/scripts.html"))
                  [:div#navigation] (html/html-content (slurp "resources/partials/navigation.html"))
                  [:div#browser-upgrade] (html/html-content (slurp "resources/partials/browser_upgrade.html"))
                  [:div#article-content] (html/html-content body)
                  [:#article-title] (html/content (header :title))
                  [:div#author-bio] (html/html-content (slurp "resources/partials/author_bio.html"))
                  [:footer#footer-content] (html/html-content (slurp "resources/partials/footer.html"))
                  [:time#post-timestamp] (html/set-attr :datetime (tf/unparse (tf/formatters :date-time-no-ms) (header :date)))
                  [:time#post-timestamp] (html/content (tf/unparse (tf/formatter "EEE, dd MMM yyyy HH:mm") (header :date)))
                  [:div#feature-image] (if (nil? (header :image))
                                           nil
                                           #(assoc-in % [:content 1 :content 1 :attrs :src] (header :image))))

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

(defn post-filename [post]
  (filename (get-in post [:header :slug])
            (get-in post [:header :date])))

(defn post-relative-url [post]
  (str (tf/unparse (tf/formatter "/yyyy/MM/dd/") (get-in post [:header :date]))
       (get-in post [:header :slug])
       "/"))

(defn post-lead [post len]
  (let [post-text (apply str (html/texts (html/html-snippet (post :body))))]
    (apply str (concat (take len (str/trim post-text)) "…"))))

(defn rss-content [post]
  (assoc post :rss-content (post :body)))

(defn add-post-lead [post]
  (assoc post :lead (post-lead post lead-length)))

(defn filename-body-map [post]
  {(post-filename post) (post :body)})

(defn apply-post-layout [post]
  (assoc post :body (apply str (post-template (post :header) (post :body)))))

(defn posts-by-date [posts]
  (sort #(compare (-> %2 :header :date) (-> %1 :header :date)) posts))

(defn get-posts []
    (->> (stasis/slurp-directory "posts/" #".*\.(md|markdown)$")
         (vals)
         (map #(-> % parse-post
                     markdown
                     enliveify
                     highlight
                     render
                     add-post-lead
                     rss-content
                     apply-post-layout))))

