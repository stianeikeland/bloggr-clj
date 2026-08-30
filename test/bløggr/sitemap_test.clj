(ns bløggr.sitemap-test
  (:use midje.sweet)
  (:require [bløggr.sitemap :refer [get-sitemap]])
  (:import [java.time OffsetDateTime]))

(fact "get-sitemap emits loc with publish-date lastmod per entry"
  (let [settings {:base-url "https://example.com"}
        sitemap (get-sitemap settings
                             {"/2007/08/28/my-post/" (OffsetDateTime/parse "2007-08-28T01:59:36+00:00")
                              "/" (OffsetDateTime/parse "2026-08-25T12:00:00+00:00")})]
    sitemap => (contains "<loc>https://example.com/2007/08/28/my-post/</loc>")
    sitemap => (contains "<lastmod>2007-08-28</lastmod>")
    sitemap => (contains "<loc>https://example.com/</loc>")
    sitemap => (contains "<lastmod>2026-08-25</lastmod>")))

(fact "get-sitemap does not emit changefreq"
  (get-sitemap {:base-url "https://example.com"} {"/" (OffsetDateTime/now)})
  =not=> (contains "changefreq"))
