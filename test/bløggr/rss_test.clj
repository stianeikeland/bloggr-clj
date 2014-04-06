(ns bløggr.rss-test
  (:use midje.sweet)
  (:require [bløggr.rss :refer :all]
            [clj-time.core :as time]))

(def settings
  {:site-title "abc"
   :site-description "def"
   :base-url "http://url.com"
   :author "ghij"})

(def posts
  [{:header {:title "Post A title"
             :slug "post_a_title"
             :date (time/date-time 2000 1 2 3 4 5 6)}
    :rss-content "Post A content"}])

(fact "get-rss renders xml with site details"
  (let [rss (get-rss settings [])]
    rss => (contains "<title>\nabc\n</title>")
    rss => (contains "<link>\nhttp://url.com\n</link>")
    rss => (contains "<description>\ndef\n</description>")))

(fact "get-rss renders xml with posts"
  (let [rss (get-rss settings posts)]
    rss => (contains "<title>\nPost A title\n</title>")
    rss => (contains "<description>\nPost A content\n</description>")
    rss => (contains "<pubDate>")
    rss => (contains "<link>\nhttp://url.com/2000/01/02/post_a_title/\n</link>")))

(fact "get-rss should change relative image paths to absolute paths"
  (get-rss settings [(assoc (first posts) :rss-content "<img src=\"/images/blah.jpg\">")]) =>
  (contains "http://url.com/images/blah.jpg"))
