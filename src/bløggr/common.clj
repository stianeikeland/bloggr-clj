(ns bløggr.common
  (:require [bløggr.assets :as assets]
            [bløggr.pygmentize :as pygmentize]
            [bløggr.settings :refer [settings]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io])
  (:import [com.vladsch.flexmark.parser Parser]
           [com.vladsch.flexmark.html HtmlRenderer]
           [com.vladsch.flexmark.util.data MutableDataSet]
           [com.vladsch.flexmark.ext.autolink AutolinkExtension]
           [com.vladsch.flexmark.ext.tables TablesExtension]))

(def ^:private md-options
  (doto (MutableDataSet.)
    (.set Parser/EXTENSIONS [(AutolinkExtension/create)
                             (TablesExtension/create)])
    (.set HtmlRenderer/FENCED_CODE_LANGUAGE_CLASS_PREFIX "")))

(def ^:private md-parser (.build (Parser/builder md-options)))
(def ^:private md-renderer (.build (HtmlRenderer/builder md-options)))

(defn- md->html [text]
  (.render md-renderer (.parse md-parser text)))

(def ^:private slurp-cache (atom {}))

(defn- cached-slurp
  "Slurp a file, re-reading it only when its modification time changes."
  [filename]
  (let [lm (.lastModified (io/file filename))
        [cached-lm content] (get @slurp-cache filename)]
    (if (and cached-lm (= cached-lm lm))
      content
      (let [content (slurp filename)]
        (swap! slurp-cache assoc filename [lm content])
        content))))

(defn- partial-content [filename]
  (html/html-content (cached-slurp filename)))

(defn- partial-nodes [filename]
  (html/html-snippet (cached-slurp filename)))

(defn- fingerprint-css-link [node]
  (let [href (get-in node [:attrs :href])]
    (if-let [fingerprint (get (assets/css-fingerprints) href)]
      (assoc-in node [:attrs :href] (str href "?v=" fingerprint))
      node)))

(defn page-scaffold
  "Shared page chrome (head, scripts, nav, bio, footer) as an enlive transformation."
  [title description]
  (fn [node]
    (html/at node
      [:head] (partial-content "resources/partials/head.html")
      [:head :link] fingerprint-css-link
      [:title] (html/content title)
      [[:meta (html/attr= :name "description")]] (html/set-attr :content description)
      [:div#scripts] (html/substitute (partial-nodes "resources/partials/scripts.html"))
      [:header#navigation] (html/substitute (partial-nodes "resources/partials/navigation.html"))
      [:.author-bio] (partial-content "resources/partials/author_bio.html")
      [:a.bio-link] (html/set-attr :href (:author-url settings))
      [:.bio-name :a] (html/content (:author settings))
      [:img.bio-photo] (html/set-attr :alt (str (:author settings) " bio photo"))
      [:footer#footer-content] (partial-content "resources/partials/footer.html")
      [:.footer-author] (html/content (:author settings)))))

(defn- update-body [f post]
  (assoc post :body (f (post :body))))

(defn markdown [post]
  (update-body md->html post))

(defn- node-text [n]
  (if (string? n)
    n
    (apply str (map node-text (:content n)))))

(defn- highlight-node [n]
  (if-let [lang (some-> n :content first :attrs :class)]
    (if-let [out (pygmentize/highlight (node-text n) lang)]
      (vec (html/html-snippet out))
      n)
    n))

(defn highlight [post]
  (update-body #(html/at % [:pre] highlight-node) post))

(def enliveify (partial update-body html/html-snippet))

(defn render [post]
  (update-body #(apply str (html/emit* %)) post))
