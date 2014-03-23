(ns bløggr.core
  (:require [stasis.core :as stasis]
            [clojure.string :as str]
            [me.raynes.cegdown :as md]
            [clj-time.format :as tf]
            [clj-time.core :as t]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]
            [clojure.java.io :as io]
            [optimus.prime :as optimus]
            [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.strategies :as strategies]))

(def cegdown-ext [:fenced-code-blocks :autolinks])

(html/deftemplate post-template "layouts/post.html" [header body]
                  [:head] (html/html-content (slurp "resources/partials/head.html"))
                  [:div#scripts] (html/html-content (slurp "resources/partials/scripts.html"))
                  [:div#navigation] (html/html-content (slurp "resources/partials/navigation.html"))
                  [:div#browser-upgrade] (html/html-content (slurp "resources/partials/browser_upgrade.html"))
                  [:div#article-content] (html/html-content body)
                  [:#article-title] (html/content (header :title))
                  [:div#author-bio] (html/html-content (slurp "resources/partials/author_bio.html"))
                  [:footer#footer-content] (html/html-content (slurp "resources/partials/footer.html"))
                  [:time#post-timestamp] (html/set-attr :datetime (tf/unparse (tf/formatters :date-time-no-ms) (header :date)))
                  [:time#post-timestamp] (html/content (tf/unparse (tf/formatter "EEE, dd MMM yyyy HH:mm") (header :date)))
                  [:div#feature-image] (if (nil? (header :image))
                                           nil
                                           #(assoc-in % [:content 1 :content 1 :attrs :src] (header :image))))

(defn parse-datestring [date-str]
  (tf/parse (tf/formatter "yyyy-MM-dd HH:mm:ssZ") date-str))

(defn parse-post
  "Parse a blog post into header map and body string. Convert string date to DateTime"
  [post]
  (let [x (str/split post #"\n------\n")]
       {:body (second x)
        :header (let [header (read-string (first x))]
                     (assoc header :date (parse-datestring (header :date))))}))

(defn filename [title date]
  (str (tf/unparse (tf/formatter "/yyyy/MM/dd/") date)
       title "/index.html"))

(defn filename-body-map [post]
  {(filename (get-in post [:header :slug])
             (get-in post [:header :date])) (post :body)})

(defn markdown [post]
  (assoc post :body (md/to-html (post :body) cegdown-ext)))

(defn- highlight-node [n]
  (let [lang (:class (:attrs n))]
    (if (nil? lang) n
        (assoc n :content
              (html/html-snippet (pygments/highlight (apply str (:content n))
                                                                 (keyword lang)
                                                                 :html))))))
(defn highlight [post]
  (assoc post :body (html/at (post :body) [:pre :code] highlight-node)))

(defn enliveify [post]
  (assoc post :body (html/html-snippet (post :body))))

(defn render [post]
  (assoc post :body (apply str (html/emit* (post :body)))))

(defn apply-post-layout [post]
  (assoc post :body (apply str (post-template (post :header) (post :body)))))

(defn get-posts []
    (->> (stasis/slurp-directory "posts/" #"test.*\.(md|markdown)$")
         (vals)
         (map #(-> % parse-post
                     markdown
                     enliveify
                     highlight
                     render
                     apply-post-layout
                     filename-body-map))
         (reduce merge)))


(defn get-css []
  (stasis/slurp-directory "resources/css/" #".*\.css"))

(defn get-page-sources []
  (hash-map :posts (get-posts) :css (get-css)))

(defn get-assets []
  (reduce merge (map #(assoc % :path (str "/images" (% :path))) (assets/load-assets "images" [#".*"]))
                (map #(assoc % :path (str "/fonts" (% :path))) (assets/load-assets "fonts" [#".*"]))))


(def ring (-> (get-page-sources)
              (stasis/merge-page-sources)
              (stasis/serve-pages)
              (optimus/wrap get-assets optimizations/none strategies/serve-live-assets)))
