(ns bløggr.sitemap
  (:require [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clojure.data.xml :as xml]))

(def urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"
             :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
             :xsi:schemaLocation "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"})

(defn- get-current-iso-8601-date []
  (let [current-date-time (time/to-time-zone (time/now) (time/default-time-zone))
        formatter (time-format/formatters :date-time-no-ms)
        with-zone (time-format/with-zone formatter (.getZone current-date-time))]
    (time-format/unparse with-zone current-date-time)))

(defn- trim-index-html [path]
  (clojure.string/replace path #"/index.html$" "/"))

(defn- url [{base :base-url} path]
  (xml/element
   :url {}
   (xml/element :loc {} (trim-index-html (str base path)))
   (xml/element :lastmod {} (get-current-iso-8601-date))
   (xml/element :changefreq {} "daily")))

(defn get-sitemap [settings paths]
  (xml/emit-str (xml/element :urlset urlset
                             (map (partial url settings) paths))))
