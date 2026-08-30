(ns bløggr.rss
  (:require [bløggr.posts :as p]
            [clj-rss.core :as cljrss]
            [clojure.string :as str])
  (:import [java.time Instant]))

(defn- rss-header [settings]
  {:title (settings :site-title)
   :link (settings :base-url)
   :description (settings :site-description)
   :lastBuildDate (Instant/now)})

(defn- fix-relative-image-urls [baseurl content]
  (str/replace content
               " src=\"/images/"
               (str " src=\"" baseurl "/images/")))

(defn- rss-post [settings post]
  {:title (get-in post [:header :title])
   :description (fix-relative-image-urls (settings :base-url) (post :rss-content))
   :link (str (settings :base-url) (p/post-relative-url post))
   :pubDate (.toInstant (get-in post [:header :date]))
   :guid [{:isPermaLink false} (p/post-relative-url post)]})

(defn get-rss [settings posts]
  (let [header (rss-header settings)
        rss-posts (map (partial rss-post settings)
                       (take 10 posts))]
    (apply cljrss/channel-xml (cons header rss-posts))))
