(ns bløggr.common
  (:require [me.raynes.cegdown :as md]
            [clj-time.format :as tf]
            [net.cgrand.enlive-html :as html]
            [clygments.core :as pygments]
            [clojure.java.io :as io]))

(def cegdown-ext [:fenced-code-blocks :autolinks])

(defn parse-datestring [date-str]
  (tf/parse (tf/formatter "yyyy-MM-dd HH:mm:ssZ") date-str))

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
