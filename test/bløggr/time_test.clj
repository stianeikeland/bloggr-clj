(ns bløggr.time-test
  (:use midje.sweet)
  (:require [bløggr.time :refer [parse-datestring utc-format]])
  (:import [java.time OffsetDateTime]))

(fact "parse-datestring parses string to date"
  (parse-datestring "2007-08-28 01:59:36+00:00") =>
  (OffsetDateTime/parse "2007-08-28T01:59:36+00:00"))

(fact "parse-datestring accepts offsets without colon"
  (parse-datestring "2015-07-20 14:00:00+0200") =>
  (OffsetDateTime/parse "2015-07-20T14:00:00+02:00"))

(fact "parse-datestring accepts Z as zero offset"
  (parse-datestring "2007-08-28 01:59:36Z") =>
  (OffsetDateTime/parse "2007-08-28T01:59:36+00:00"))

(fact "utc-format renders dates in UTC regardless of offset"
  (.format (utc-format "yyyy-MM-dd'T'HH:mm:ssXX")
           (OffsetDateTime/parse "2014-07-22T08:00:00+01:00")) =>
  "2014-07-22T07:00:00Z")
