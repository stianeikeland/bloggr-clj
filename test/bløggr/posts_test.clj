(ns bløggr.posts-test
  (:use midje.sweet)
  (:require [bløggr.posts :refer :all]
            [clj-time.core :as t]))

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


(def blogdate (t/from-time-zone (t/date-time 2007 8 28 1 59 36)
                                (t/time-zone-for-offset 0)))

(fact "filename-body-map turns a post into title => body map"
  (filename-body-map {:header {:slug "really-cool-post" :date blogdate} :body "post body"}) =>
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
  (let [content (:body
                 (apply-post-layout {:body "this is the body"
                                     :header {:title "post title"
                                              :date blogdate}}))]
    content => (contains "this is the body")
    content => (contains "<h1 id=\"article-title\">post title</h1>")
    content => (contains "<time datetime=\"2007-08-28T01:59:36Z\" id=\"post-timestamp\">Tue, 28 Aug 2007 01:59</time>")))


(fact "post-lead extracts first x text characters of html-post"
  (post-lead {:body "<p>lorum lorum ipsum <a href='index.html'>ipsum</a></p>"} 20) =>
  "lorum lorum ipsum ip…")

(fact "posts-by-date sorts posts by date"
  (let [a {:header {:date 0}}
        b {:header {:date 1}}]
    (posts-by-date [a b]) => [b a]))
