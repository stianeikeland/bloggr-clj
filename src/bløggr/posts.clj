(ns bløggr.posts
  (:require [bløggr.common :refer :all]
            [stasis.core :as stasis]
            [clojure.string :as str]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]))

(def lead-length 500)
(def twitter-card-length 190)

(defn twitter-card-template
  "Create twitter cards in document head"
  [{header :header description :twitter-lead}]
  (html/html [:meta {:name "twitter:card"
                     :content (if (:image header) "summary_large_image" "summary")}]
             [:meta {:name "twitter:site" :content "@stianeikeland"}]
             [:meta {:name "twitter:title" :content (:title header)}]
             [:meta {:name "twitter:description" :content description}]
             [:meta {:name "twitter:creator" :content "@stianeikeland"}]
             (when (:image header)
               [:meta {:name "twitter:image:src"
                       :content (str "http://blog.eikeland.se" (:image header))}])))

(html/deftemplate post-template "layouts/post.html" [{header :header body :body :as post}]
                  [:head] (html/html-content (slurp "resources/partials/head.html"))
                  [:div#scripts] (html/html-content (slurp "resources/partials/scripts.html"))
                  [:div#navigation] (html/html-content (slurp "resources/partials/navigation.html"))
                  [:div#browser-upgrade] (html/html-content (slurp "resources/partials/browser_upgrade.html"))
                  [:div#disqus] (html/html-content (slurp "resources/partials/disqus.html"))
                  [:div#article-content] (html/html-content body)
                  [:#article-title] (html/content (header :title))
                  [:div#author-bio] (html/html-content (slurp "resources/partials/author_bio.html"))
                  [:footer#footer-content] (html/html-content (slurp "resources/partials/footer.html"))
                  [:time#post-timestamp] (html/set-attr :datetime (tf/unparse (tf/formatters :date-time-no-ms) (header :date)))
                  [:time#post-timestamp] (html/content (tf/unparse (tf/formatter "EEE, dd MMM yyyy HH:mm") (header :date)))
                  [:title] (html/content (header :title))
                  [:head] (html/append (twitter-card-template post))
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
    (apply str (concat (take len (str/trim post-text)) "..."))))

(defn rss-content [post]
  (assoc post :rss-content (post :body)))

(defn add-post-lead [post]
  (assoc post :lead (post-lead post lead-length)))

(defn add-twitter-lead [post]
  (assoc post :twitter-lead (post-lead post twitter-card-length)))

(defn post->path-map [post]
  {(post-filename post) (post :body)})

(defn apply-post-layout [post]
  (assoc post :body (apply str (post-template post))))

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
                     add-twitter-lead
                     rss-content
                     apply-post-layout))))
