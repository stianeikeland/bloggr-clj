(ns bløggr.assets-test
  (:use midje.sweet)
  (:require [bløggr.assets :refer :all]))

(fact "get-assets loads assets and modifies path"
  (get-assets) => (just [{:path "/images/image.jpg"} {:path "/fonts/font.wof"}] :in-any-order)
  (provided
    (load-assets "images") => [{:path "/image.jpg"}]
    (load-assets "fonts") => [{:path "/font.wof"}]))
