(ns bløggr.errors-test
  (:use midje.sweet)
  (:require [bløggr.errors :refer :all]))

(fact "not-found page renders with site chrome"
  (let [page (get-not-found)]
    page => (contains "<title>404")
    page => (contains "ENOENT: No such page or directory.")
    page => (contains "class=\"site-header\"")
    page => (contains "id=\"footer-content\"")
    page => (contains "<a class=\"error-home\" href=\"/\">Return to the index</a>")))
