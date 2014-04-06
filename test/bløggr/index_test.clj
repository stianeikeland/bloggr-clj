(ns bløggr.index-test
  (:use midje.sweet)
  (:require [bløggr.index :refer :all]
            [bløggr.posts :as p]))

(def test-posts
  [{:lead "Post A lead"
    :header {:title "Post A title"}}
   {:lead "Post B lead"
    :header {:title "Post B title"}}])

(fact "get-index returns rendered index page with partials added"
  (let [index (get-index [])]
    index => (contains "<meta charset=\"utf-8\" />")
    index =not=> (contains "Insert author bio here")
    index =not=> (contains "Insert footer here")))

(fact "get-index returns index page with posts added"
  (let [index (get-index test-posts)]
    index => (contains "Post A lead")
    index => (contains "Post B lead")))

(fact "index-post-template renders a post"
  (let [post (apply str (index-post-template (first test-posts)))]
    post => (contains "<span id=\"title\">Post A title</span>")
    post => (contains "title=\"Post A title\"")
    post => (contains "Post A lead")))

(fact "index-post-template adds links to post"
  (apply str (index-post-template (first test-posts))) => (contains "href=\"/the/post/url\"")
  (provided
    (p/post-relative-url anything) => "/the/post/url"))

