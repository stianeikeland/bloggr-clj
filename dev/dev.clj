(ns dev
  (:require [bløggr.core :as app]
            [ring.adapter.jetty :refer [run-jetty]]))

(defonce server (atom nil))

(defn stop []
  (when-let [s @server]
    (.stop s)
    (reset! server nil)))

(defn start
  ([] (start 3000))
  ([port]
   (stop)
   (reset! server (run-jetty #'app/ring {:port port :join? false}))
   (println (str "bløggr dev server on http://localhost:" port))))

(defn restart
  ([] (restart 3000))
  ([port]
   (stop)
   (require 'bløggr.core :reload-all)
   (start port)))

(defn -main [& [port]]
  (start (if port (parse-long port) 3000))
  @(promise))
