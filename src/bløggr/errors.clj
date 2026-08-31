(ns bløggr.errors
  (:require [bløggr.common :refer [page-scaffold]]
            [bløggr.settings :refer [settings]]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate not-found-template "layouts/404.html" []
  [:html] (page-scaffold (str "404 | " (:site-title settings)) "ENOENT: No such page or directory."))

(defn get-not-found []
  (apply str (not-found-template)))
