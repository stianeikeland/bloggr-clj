(ns bløggr.index-test
  (:use midje.sweet)
  (:require [bløggr.index :refer :all])
  (:import [java.time OffsetDateTime]))

(def ^:private test-date (OffsetDateTime/parse "2000-01-02T03:04:05+00:00"))

(def test-posts
  [{:lead "Post A lead"
    :header {:title "Post A title" :slug "post-a" :date test-date}}
   {:lead "Post B lead"
    :header {:title "Post B title" :slug "post-b" :date test-date}}])

(def test-post-with-image
  {:lead "Post C lead"
   :header {:title "Post C title"
            :slug "post-c"
            :image "/images/post-c.jpg"
            :date test-date}})

(fact "get-index returns rendered index page"
  (let [index (get-index [])]
    index => (contains "<meta charset=\"utf-8\" />")))

(fact "get-index returns index page with posts added"
  (let [index (get-index test-posts)]
    index => (contains "Post A lead")
    index => (contains "Post B lead")))

(fact "index-post-template fills the link with title and url"
  (apply str (index-post-template (first test-posts))) =>
  (contains "<a class=\"link\" href=\"/2000/01/02/post-a/\">Post A title</a>"))

(fact "index-post-template renders the lead"
  (apply str (index-post-template (first test-posts))) => (contains "Post A lead"))

(fact "index-post-template renders no thumbnail when the post has no thumb"
  (apply str (index-post-template (first test-posts))) =not=> (contains "index-image"))

(fact "index-post-template renders a linked image"
  (apply str (index-post-template test-post-with-image)) =>
  (contains "<a href=\"/2000/01/02/post-c/\"><img src=\"/images/post-c.jpg\" alt=\"Post C title\" loading=\"lazy\" /></a>"))

(fact "index-post-template prefers :thumbnail over :image"
  (apply str (index-post-template (assoc-in test-post-with-image [:header :thumbnail] "/images/post-c-front.jpg"))) =>
  (contains "src=\"/images/post-c-front.jpg\""))

(fact "get-index resolves the thumbnail for posts with an image"
  (get-index [test-post-with-image]) => (contains "/images/thumbs/post-c.jpg")
  (provided
    (thumb-for "/images/post-c.jpg") => "/images/thumbs/post-c.jpg"))

(fact "thumb-for falls back to the original when no thumbnail exists"
  (thumb-for "/images/never-generated/x.jpg") =>
  "/images/never-generated/x.jpg")

(fact "thumb-for resolves the thumbnail path when the resource exists"
  (thumb-for "/images/foo/bar.jpg") => "/images/thumbs/foo/bar.jpg"
  (provided
    (clojure.java.io/resource "images/thumbs/foo/bar.jpg") => true))

