(ns bløggr.posts-test
  (:use midje.sweet)
  (:require [bløggr.posts :refer :all]
            [net.cgrand.enlive-html :as html])
  (:import [java.time OffsetDateTime]))

(def blog-post "
  {
  :slug \"openwrt-on-hama-mpr-a1-v2-2\"
  :title \"OpenWRT on Hama MPR-A1 (v2.2)\"
  :image \"2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg\"
  :date \"2007-08-28 01:59:36+00:00\"
  :tags #{:hacking :mpr-a1 :openwrt :router}
  }

------
body content")


(def blogdate (OffsetDateTime/parse "2007-08-28T01:59:36+00:00"))

(fact "post->path-map turns a post into path => page map"
  (post->path-map {:header {:slug "really-cool-post" :date blogdate} :page "post body"}) =>
  {"/2007/08/28/really-cool-post/index.html" "post body"})

(fact "parse-post extracts header from blog post"
  (:header (parse-post blog-post)) => {:image "2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg"
                                       :slug "openwrt-on-hama-mpr-a1-v2-2"
                                       :tags #{:hacking :mpr-a1 :openwrt :router}
                                       :date blogdate
                                       :title "OpenWRT on Hama MPR-A1 (v2.2)"})

(fact "parse-post extracts body from blog post"
  (:body (parse-post blog-post)) => "body content")


(fact "apply-post-layout should apply post template to post"
  (let [content (:page
                  (apply-post-layout {:body "this is the body"
                                      :header {:title "post title"
                                               :date blogdate}}))]
    content => (contains "this is the body")
    content => (contains "<h1 class=\"article-title\">post title</h1>")
    content => (contains "<time id=\"post-timestamp\" datetime=\"2007-08-28T01:59:36Z\">Tue, 28 Aug 2007 01:59</time>")))

(fact "apply-post-layout overrides meta description and fills byline title"
  (let [content (:page
                  (apply-post-layout {:body "b"
                                      :description "a short lead"
                                      :header {:title "post title"
                                               :date blogdate}}))]
    content => (contains "<meta name=\"description\" content=\"a short lead\" />")
    content => (contains "<strong class=\"byline-title\">post title</strong>")))

(fact "apply-post-layout renders sorted tags as chips"
  (let [content (:page
                  (apply-post-layout {:body "b"
                                      :header {:title "post title"
                                               :tags #{:zeta :alpha}
                                               :date blogdate}}))]
    content => (contains "<li>#alpha</li>")
    content => (contains "<li>#zeta</li>")))

(fact "apply-post-layout drops tags list when post has no tags"
  (let [content (:page
                  (apply-post-layout {:body "b"
                                      :header {:title "post title"
                                               :date blogdate}}))]
    content =not=> (contains "article-tags")))


(fact "apply-post-layout sets feature image src and alt from header"
  (let [content (:page
                  (apply-post-layout {:body "b"
                                      :header {:title "post title"
                                               :image "/images/x.jpg"
                                               :date blogdate}}))]
    content => (contains "<img src=\"/images/x.jpg\" alt=\"post title\" />")))

(fact "apply-post-layout drops the feature image block when post has no image"
  (let [content (:page
                  (apply-post-layout {:body "b"
                                      :header {:title "post title"
                                               :date blogdate}}))]
    content =not=> (contains "feature-image")))

(fact "post-lead truncates at word boundary with ellipsis"
  (post-lead {:body (html/html-snippet "<p>lorum lorum ipsum <a href='index.html'>ipsum</a></p>")} 20) =>
  "lorum lorum ipsum…")

(fact "post-lead leaves short text unchanged without ellipsis"
  (post-lead {:body (html/html-snippet "<p>short text</p>")} 20) =>
  "short text")

(fact "post-lead hard-cuts when there is no word boundary"
  (post-lead {:body (html/html-snippet "<p>abcdefghijklmnopqrstuvwxyz</p>")} 20) =>
  "abcdefghijklmnopqrs…")

(fact "post-lead does not split surrogate pairs"
  (post-lead {:body (html/html-snippet "<p>😆😆😆😆😆😆😆😆😆😆😆</p>")} 20) =>
  "😆😆😆😆😆😆😆😆😆…")

(fact "posts-by-date sorts posts by date"
  (let [a {:header {:date 0}}
        b {:header {:date 1}}]
    (posts-by-date [a b]) => [b a]))
