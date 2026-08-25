(ns bløggr.sitemap-test
  (:use midje.sweet)
  (:require [bløggr.sitemap :refer [get-sitemap]]
            [clj-time.core :as t]))

(fact "get-sitemap emits loc with publish-date lastmod per entry"
  (let [settings {:base-url "https://example.com"}
        sitemap (get-sitemap settings
                             {"/2007/08/28/my-post/index.html" (t/date-time 2007 8 28 1 59 36)
                              "/" (t/date-time 2026 8 25 12 0)})]
    sitemap => (contains "<loc>https://example.com/2007/08/28/my-post/</loc>")
    sitemap => (contains "<lastmod>2007-08-28</lastmod>")
    sitemap => (contains "<loc>https://example.com/</loc>")
    sitemap => (contains "<lastmod>2026-08-25</lastmod>")))

(fact "get-sitemap does not emit changefreq"
  (get-sitemap {:base-url "https://example.com"} {"/" (t/now)})
  =not=> (contains "changefreq"))
