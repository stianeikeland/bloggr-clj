(ns bløggr.sitemap
  (:require [bløggr.time :refer [utc-format]]
            [clojure.data.xml :as xml]))

(def urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"
             :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
             :xsi:schemaLocation "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"})

(def ^:private lastmod-format (utc-format "yyyy-MM-dd"))

(defn- trim-index-html [path]
  (clojure.string/replace path #"/index.html$" "/"))

(defn- url [{base :base-url} [path date]]
  (xml/element
    :url {}
    (xml/element :loc {} (trim-index-html (str base path)))
    (xml/element :lastmod {} (.format lastmod-format date))))

(defn get-sitemap
  "Build a sitemap from a {path -> lastmod-date} map."
  [settings path->lastmod]
  (xml/emit-str (xml/element :urlset urlset
                  (map (partial url settings) path->lastmod))))
