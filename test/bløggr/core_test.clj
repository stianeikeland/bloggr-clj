(ns bløggr.core-test
  (:use midje.sweet)
  (:require [bløggr.core :refer :all]
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


(fact "parse-datestring parses string to date"
      (parse-datestring "2007-08-28 01:59:36+00:00") => blogdate)

(fact "parse-post extracts header from blog post"
      (:header (parse-post blog-post)) => {:image "2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg"
                                           :slug "openwrt-on-hama-mpr-a1-v2-2"
                                           :tags #{:hacking :mpr-a1 :openwrt :router}
                                           :date blogdate
                                           :title "OpenWRT on Hama MPR-A1 (v2.2)"})

(fact "parse-post extracts body from blog post"
      (:body (parse-post blog-post)) => "body content")

(fact "filename-body-map turns a post into title => body map"
      (filename-body-map {:header {:slug "really-cool-post" :date blogdate} :body "post body"}) =>
      {"/2007/08/28/really-cool-post/index.html" "post body"})
