(ns bløggr.index
  (:require [bløggr.common :refer :all]
            [bløggr.posts :as p]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate index-post-template "partials/index_post.html" [post]
  [:span#title] (html/content (-> post :header :title))
  [:p#lead] (html/content (post :lead))
  [:a#link] (html/set-attr :href (p/post-relative-url post))
  [:a#link] (html/set-attr :title (-> post :header :title)))

(html/deftemplate index-template "layouts/index.html" [posts]
  [:head] (html/html-content (slurp "resources/partials/head.html"))
  [:div#scripts] (html/html-content (slurp "resources/partials/scripts.html"))
  [:div#navigation] (html/html-content (slurp "resources/partials/navigation.html"))
  [:div#browser-upgrade] (html/html-content (slurp "resources/partials/browser_upgrade.html"))
  [:div#author-bio] (html/html-content (slurp "resources/partials/author_bio.html"))
  [:footer#footer-content] (html/html-content (slurp "resources/partials/footer.html"))
  [:div#articles] (html/html-content (apply str (map #(apply str (index-post-template %)) posts))))

(defn get-index [posts]
  (apply str (index-template posts)))

