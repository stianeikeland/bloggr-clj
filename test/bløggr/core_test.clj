(ns bløggr.core-test
  (:use midje.sweet)
  (:require [bløggr.core :refer :all]))

(def blog-post "
  {
  :slug \"openwrt-on-hama-mpr-a1-v2-2\"
  :title \"OpenWRT on Hama MPR-A1 (v2.2)\"
  :image \"2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg\"
  :tags #{:hacking :mpr-a1 :openwrt :router}
  }

------
body content")

(fact "parse-post extracts header from blog post"
      (:header (parse-post blog-post)) => {:image "2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg"
                                           :slug "openwrt-on-hama-mpr-a1-v2-2"
                                           :tags #{:hacking :mpr-a1 :openwrt :router}
                                           :title "OpenWRT on Hama MPR-A1 (v2.2)"})

(fact "parse-post extracts body from blog post"
      (:body (parse-post blog-post)) => "body content")
