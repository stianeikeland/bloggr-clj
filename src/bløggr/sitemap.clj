(ns bløggr.sitemap
  (:require [clj-time.format :as time-format]
            [clojure.data.xml :as xml]))

(def urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"
             :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
             :xsi:schemaLocation "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"})

(def ^:private lastmod-format (time-format/formatter "yyyy-MM-dd"))

(defn- trim-index-html [path]
  (clojure.string/replace path #"/index.html$" "/"))

(defn- url [{base :base-url} [path date]]
  (xml/element
    :url {}
    (xml/element :loc {} (trim-index-html (str base path)))
    (xml/element :lastmod {} (time-format/unparse lastmod-format date))))

(defn get-sitemap
  "Build a sitemap from a {path -> lastmod-date} map."
  [settings path->lastmod]
  (xml/emit-str (xml/element :urlset urlset
                  (map (partial url settings) path->lastmod))))
