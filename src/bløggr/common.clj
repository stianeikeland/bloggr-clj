(ns bløggr.common
  (:require [me.raynes.cegdown :as md]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]
            [clojure.java.io :as io]))

(def cegdown-ext [:fenced-code-blocks :autolinks])

(defn parse-datestring [date-str]
  (tf/parse (tf/formatter "yyyy-MM-dd HH:mm:ssZ") date-str))

(defn update-body [f post]
  (assoc post :body (f (post :body))))

(defn markdown [post]
  (update-body #(md/to-html % cegdown-ext) post))

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
