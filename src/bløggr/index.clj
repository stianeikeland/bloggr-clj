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
  [:html] (page-scaffold (:site-title settings) (:site-description settings))
  [:div#articles] (html/substitute (html/html-snippet (apply str (map #(apply str (index-post-template %)) posts)))))

(defn get-index [posts]
  (apply str (index-template posts)))
