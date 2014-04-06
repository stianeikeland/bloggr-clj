(ns bløggr.rss
  (:require [bløggr.posts :as p]
            [clj-rss.core :as cljrss]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]))

(defn- rss-header [settings]
  {:title (settings :site-title)
   :link (settings :base-url)
   :description (settings :site-description)
   :lastBuildDate (tc/to-date (time/now))})

(defn- fix-relative-image-urls [baseurl content]
  (clojure.string/replace content
                          " src=\"/images/"
                          (str " src=\"" baseurl "/images/")))

(defn- rss-post [settings post]
  {:title (get-in post [:header :title])
   :description (fix-relative-image-urls (settings :base-url) (post :rss-content))
   :link (str (settings :base-url) (p/post-relative-url post))
   :pubDate (tc/to-date (get-in post [:header :date]))
   :guid [{:isPermaLink false} (p/post-relative-url post)]})

(defn get-rss [settings posts]
  (let [header (rss-header settings)
        rss-posts (map (partial rss-post settings)
                       (take 10 (p/posts-by-date posts)))]
    (apply cljrss/channel-xml (cons header rss-posts))))



