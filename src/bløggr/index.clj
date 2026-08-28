(ns bløggr.index
  (:require [bløggr.common :refer :all]
            [bløggr.posts :as p]
            [bløggr.settings :refer [settings]]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]))

(defn format-post-date [date]
  (-> "MMMM dd, yyyy." tf/formatter (tf/with-locale java.util.Locale/ENGLISH) (tf/unparse date)))

(html/deftemplate index-post-template "partials/index_post.html"
  [{:keys [header lead] :as post}]
  [:a.link] (html/content (:title header))
  [:a.link] (html/set-attr :href (p/post-relative-url post))
  [:time.date] (html/content (format-post-date (:date header)))
  [:time.date] (html/set-attr :datetime (tf/unparse (tf/formatters :date) (:date header)))
  [:p.lead] (html/content lead))

(html/deftemplate index-template "layouts/index.html" [posts]
  [:head] (html/html-content (cached-slurp "resources/partials/head.html"))
  [:title] (html/content (:site-title settings))
  [[:meta (html/attr= :name "description")]] (html/set-attr :content (:site-description settings))
  [:div#scripts] (html/substitute (html/html-snippet (cached-slurp "resources/partials/scripts.html")))
  [:header#navigation] (html/substitute (html/html-snippet (cached-slurp "resources/partials/navigation.html")))
  [:.author-bio] (html/html-content (cached-slurp "resources/partials/author_bio.html"))
  [:a.bio-link] (html/set-attr :href (:author-url settings))
  [:.bio-name :a] (html/content (:author settings))
  [:img.bio-photo] (html/set-attr :alt (str (:author settings) " bio photo"))
  [:footer#footer-content] (html/html-content (cached-slurp "resources/partials/footer.html"))
  [:.footer-author] (html/content (:author settings))
  [:div#articles] (html/substitute (html/html-snippet (apply str (map #(apply str (index-post-template %)) posts)))))

(defn get-index [posts]
  (apply str (index-template posts)))
