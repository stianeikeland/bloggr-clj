(ns bløggr.rss-test
  (:use midje.sweet)
  (:require [bløggr.rss :refer :all])
  (:import [java.time OffsetDateTime]))

(def settings
  {:site-title "abc"
   :site-description "def"
   :base-url "http://url.com"
   :author "ghij"})

(def posts
  [{:header {:title "Post A title"
             :slug "post_a_title"
             :date (OffsetDateTime/parse "2000-01-02T03:04:05Z")}
    :rss-content "Post A content"}])

(fact "get-rss renders xml with site details"
  (let [rss (get-rss settings [])]
    rss => (contains "<title>abc</title>")
    rss => (contains "<link>http://url.com</link>")
    rss => (contains "<description>def</description>")))

(fact "get-rss renders xml with posts"
  (let [rss (get-rss settings posts)]
    rss => (contains "<title>Post A title</title>")
    rss => (contains "<description>Post A content</description>")
    rss => (contains "<pubDate>")
    rss => (contains "<link>http://url.com/2000/01/02/post_a_title/</link>")))

(fact "get-rss should change relative image paths to absolute paths"
  (get-rss settings [(assoc (first posts) :rss-content "<img src=\"/images/blah.jpg\">")]) =>
  (contains "http://url.com/images/blah.jpg"))
