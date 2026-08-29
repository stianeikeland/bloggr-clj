(ns bløggr.assets-test
  (:use midje.sweet)
  (:require [bløggr.assets :refer :all]))

(fact "get-assets loads assets and modifies path"
  (get-assets) => (just [{:path "/js/script.js"}
                         {:path "/images/image.jpg"}
                         {:path "/fonts/font.wof"}]
                        :in-any-order)
  (provided
    (load-assets "js") => [{:path "/script.js"}]
    (load-assets "images") => [{:path "/image.jpg"}]
    (load-assets "fonts") => [{:path "/font.wof"}]))

(fact "css-fingerprints returns sha-1 per css path"
  (css-fingerprints) => {"/style.css" "a4c0dac49e47ffe0dbcca7615f73b72ef6b71543"
                         "/pygments.css" "42cc16f1d09dd1c171a64d8400b2c60fe3e025f8"}
  (provided
    (get-css) => {"/style.css" "body{}"
                  "/pygments.css" ".highlight{}"}))
