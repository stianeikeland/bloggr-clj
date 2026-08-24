(ns bløggr.common
  (:require [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]
            [clojure.java.io :as io])
  (:import [com.vladsch.flexmark.parser Parser]
           [com.vladsch.flexmark.html HtmlRenderer]
           [com.vladsch.flexmark.util.data MutableDataSet]
           [com.vladsch.flexmark.ext.autolink AutolinkExtension]
           [com.vladsch.flexmark.ext.tables TablesExtension]))

(defn md->html [text]
  (let [options (doto (MutableDataSet.)
                  (.set Parser/EXTENSIONS [(AutolinkExtension/create)
                                           (TablesExtension/create)])
                  (.set HtmlRenderer/FENCED_CODE_LANGUAGE_CLASS_PREFIX ""))
        parser (.build (Parser/builder options))
        renderer (.build (HtmlRenderer/builder options))]
    (.render renderer (.parse parser text))))

(defn parse-datestring [date-str]
  (tf/parse (tf/formatter "yyyy-MM-dd HH:mm:ssZ") date-str))

(defn update-body [f post]
  (assoc post :body (f (post :body))))

(defn markdown [post]
  (update-body md->html post))

(defn- highlight-node [n]
  (let [lang (:class (:attrs n))]
    (if (nil? lang) n
        (assoc n :content
              (html/html-snippet (pygments/highlight (apply str (:content n))
                                                                 (keyword lang)
                                                                 :html))))))
(defn highlight [post]
  (update-body #(html/at % [:pre :code] highlight-node) post))

(def enliveify (partial update-body html/html-snippet))

(defn render [post]
  (update-body #(apply str (html/emit* %)) post))

(defn strip-comments [body]
  (html/sniptest body [html/comment-node] nil))

(defn post-strip-comments [post]
  (update-body strip-comments post))
