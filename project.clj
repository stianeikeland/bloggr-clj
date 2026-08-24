(defproject bløggr "0.1.0-SNAPSHOT"
  :description "Blog engine for eikeland.se"
  :url "http://eikeland.se/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [stasis "2023.11.21"]
                 [ring "1.15.5"]
                 [ring/ring-codec "1.3.0"]
                 [com.vladsch.flexmark/flexmark-all "0.64.8"]
                 [clygments "2.0.2"]
                 [enlive "1.1.6"]
                 [optimus "2026.05.27"]
                 [clj-rss "0.4.0"]
                 [clj-time "0.15.2"]
                 [org.clojure/data.xml "0.0.8"]]
  :ring {:handler bløggr.core/ring
         :open-browser? false}
  :aliases {"export" ["run" "-m" "bløggr.core/export"]}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]
                             [lein-midje "3.2.2"]]
                   :dependencies [[midje "1.10.10" :exclusions [org.clojure/clojure]]]}})
