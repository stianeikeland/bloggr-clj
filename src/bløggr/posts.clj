(ns bløggr.posts
  (:require [bløggr.comments :as comments]
            [bløggr.common :refer [enliveify highlight markdown page-scaffold render]]
            [bløggr.settings :refer [settings]]
            [bløggr.time :refer [parse-datestring utc-format]]
            [stasis.core :as stasis]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html]))

(def lead-length 500)
(def twitter-card-length 190)

(def ^:private post-date-path-format (utc-format "/yyyy/MM/dd/"))
(def ^:private datetime-attr-format (utc-format "yyyy-MM-dd'T'HH:mm:ssXX"))
(def ^:private byline-format (utc-format "EEE, dd MMM yyyy HH:mm" java.util.Locale/ENGLISH))

(defn- twitter-card-template
  "Create twitter cards in document head"
  [{header :header description :twitter-lead}]
  (let [img (:image header)
        card {:card (if img "summary_large_image" "summary")
              :site "@stianeikeland"
              :title (:title header)
              :description description
              :creator "@stianeikeland"
              :image:src (when img (str (:base-url settings) img))}
        twitter-card (for [[k v] card]
                       (when v
                         [:meta {:name (str "twitter:" (name k)) :content v}]))]
    (apply html/html twitter-card)))

(defn post-relative-url [post]
  (str (.format post-date-path-format (get-in post [:header :date]))
       (get-in post [:header :slug])
       "/"))

(defn post-absolute-url [post]
  (str (:base-url settings) (post-relative-url post)))

(defn- post-text [post]
  (apply str (html/texts (html/html-snippet (post :body)))))

(defn- open-graph
  [{header :header description :twitter-lead :as post}]
  (let [img (:image header)
        graph-data {:title (:title header)
                    :type "article"
                    :locale "en_US"
                    :site_name (.getHost (java.net.URI. (:base-url settings)))
                    :description description
                    :image (when img (str (:base-url settings) img))
                    :url (post-absolute-url post)
                    :video (:video header)}
        graph (for [[k v] graph-data]
                (when v
                  [:meta {:property (str "og:" (name k)) :content v}]))]

    (apply html/html graph)))


(html/deftemplate post-template "layouts/post.html" [{:keys [header body] :as post}]
  [:html] (page-scaffold (header :title) (post :twitter-lead))
  [:.article-title] (html/content (header :title))
  [:div#comments-anchor] (html/substitute (html/html-snippet (or (comments/comments-html (post-relative-url post))
                                                                 "")))
  [:.article-content] (html/prepend (html/html-snippet body))
  [:time#post-timestamp] (html/set-attr :datetime (.format datetime-attr-format (header :date)))
  [:time#post-timestamp] (html/content (.format byline-format (header :date)))
  [:.byline-title] (html/content (header :title))
  [:.byline-author] (html/content (:author settings))
  [:.byline-author] (html/set-attr :href (:author-url settings))
  [:head] (html/append (twitter-card-template post))
  [:head] (html/append (open-graph post))
  [:.article-tags] (if-let [tags (seq (:tags header))]
                     (html/html-content (->> tags
                                             (map name)
                                             sort
                                             (map #(str "<li>#" % "</li>"))
                                             (apply str)))
                     nil)
  [:figure#feature-image] (if-let [image (:image header)]
                            #(html/at % [:img] (html/set-attr :src image :alt (header :title)))
                            nil))

(defn parse-post
  "Parse a blog post into header map and body string. Convert string date to OffsetDateTime"
  [post]
  (let [[header body] (str/split post #"\n------\n" 2)
        header (edn/read-string header)]
    {:body body
     :header (assoc header :date (parse-datestring (header :date)))}))

(defn post-filename [post]
  (str (post-relative-url post) "index.html"))

(defn- truncate-at-word
  "Truncate s to at most len chars, cutting at a word boundary and
  appending an ellipsis only when text was actually cut."
  [s len]
  (let [s (str/trim s)]
    (if (<= (count s) len)
      s
      (let [cut (if (and (> len 1) (Character/isHighSurrogate (.charAt s (- len 2))))
                  (- len 2)
                  (dec len))
            head (subs s 0 cut)
            space (str/last-index-of head " ")]
        (if space
          (str (str/trimr (subs head 0 space)) "…")
          (str head "…"))))))

(defn post-lead [post len]
  (truncate-at-word (post-text post) len))

(defn rss-content [post]
  (assoc post :rss-content (post :body)))

(defn- add-leads [post]
  (let [text (post-text post)]
    (assoc post
           :lead (truncate-at-word text lead-length)
           :twitter-lead (truncate-at-word text twitter-card-length))))

(defn post->path-map [post]
  {(post-filename post) (post :body)})

(defn apply-post-layout [post]
  (assoc post :body (apply str (post-template post))))

(defn posts-by-date [posts]
  (sort #(compare (-> %2 :header :date) (-> %1 :header :date)) posts))

(defn get-posts []
  (->> (stasis/slurp-directory "posts/" #".*\.(md|markdown)$")
       (vals)
       (map parse-post)
       (remove (comp :draft :header))
       (map (comp apply-post-layout
                  rss-content
                  add-leads
                  render
                  highlight
                  enliveify
                  markdown))
       posts-by-date))
