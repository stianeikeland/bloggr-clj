(ns bløggr.index
  (:require [bløggr.common :refer [canonical-link page-scaffold]]
            [bløggr.posts :as p]
            [bløggr.settings :refer [settings]]
            [bløggr.time :refer [utc-format]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html]))

(def ^:private display-format (utc-format "MMMM dd, yyyy." java.util.Locale/ENGLISH))
(def ^:private datetime-attr-format (utc-format "yyyy-MM-dd"))

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
  [:time.date] (html/content (.format display-format (:date header)))
  [:time.date] (html/set-attr :datetime (.format datetime-attr-format (:date header)))
  [:p.lead] (html/content lead)
  [:figure.index-image] (if-let [thumb (some->> (:thumbnail header (:image header)) thumb-for)]
                          (index-image thumb (:title header) (p/post-relative-url post))
                          nil))

(html/deftemplate index-template "layouts/index.html" [posts]
  [:html] (page-scaffold (:site-title settings) (:site-description settings))
  [:head] (html/append (canonical-link "/"))
  [:div#articles] (html/substitute (html/html-snippet (apply str (map #(apply str (index-post-template %)) posts)))))

(defn get-index [posts]
  (apply str (index-template posts)))
