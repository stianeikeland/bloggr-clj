(ns bløggr.index
  (:require [bløggr.common :refer :all]
            [bløggr.posts :as p]
            [bløggr.settings :refer [settings]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]))

(defn format-post-date [date]
  (-> "MMMM dd, yyyy." tf/formatter (tf/with-locale java.util.Locale/ENGLISH) (tf/unparse date)))

(defn thumb-for
  "Thumbnail path for an /images/... URL, or the original when none exists."
  [path]
  (if-not (str/starts-with? path "/images/")
    path
    (let [rel (subs path (count "/images"))]
      (if (io/resource (str "images/thumbs" rel))
        (str "/images/thumbs" rel)
        path))))

(defn- index-image [thumb title url]
  (fn [node]
    (html/at node
             [:img] (html/set-attr :src thumb :alt title :loading "lazy")
             [:a] (html/set-attr :href url))))

(html/deftemplate index-post-template "partials/index_post.html"
  [{:keys [header lead] :as post}]
  [:a.link] (html/content (:title header))
  [:a.link] (html/set-attr :href (p/post-relative-url post))
  [:time.date] (html/content (format-post-date (:date header)))
  [:time.date] (html/set-attr :datetime (tf/unparse (tf/formatters :date) (:date header)))
  [:p.lead] (html/content lead)
  [:figure.index-image] (if-let [thumb (some->> (:thumbnail header (:image header)) thumb-for)]
                          (index-image thumb (:title header) (p/post-relative-url post))
                          nil))

(html/deftemplate index-template "layouts/index.html" [posts]
  [:html] (page-scaffold (:site-title settings) (:site-description settings))
  [:div#articles] (html/substitute (html/html-snippet (apply str (map #(apply str (index-post-template %)) posts)))))

(defn get-index [posts]
  (apply str (index-template posts)))
