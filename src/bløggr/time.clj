(ns bløggr.time
  (:import [java.time OffsetDateTime ZoneOffset]
           [java.time.format DateTimeFormatter]))

(defn utc-format
  "DateTimeFormatter for `pattern` (with optional locale) that renders dates in UTC."
  [pattern & [locale]]
  (.withZone (DateTimeFormatter/ofPattern pattern (or locale java.util.Locale/ROOT))
             ZoneOffset/UTC))

(def ^:private post-date-format
  (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss[XXX][XX][X]"))

(defn parse-datestring
  "Parse a post header datestring (\"yyyy-MM-dd HH:mm:ssZ\" with the offset to OffsetDateTime."
  [date-str]
  (OffsetDateTime/parse date-str post-date-format))
