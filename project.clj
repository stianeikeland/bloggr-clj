(defproject bløggr "0.1.0-SNAPSHOT"
  :description "Blog engine for eikeland.se"
  :url "http://eikeland.se/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [org.slf4j/slf4j-simple "2.0.17"]
                 [stasis "2023.11.21"]
                 [ring "1.15.5"]
                 [com.vladsch.flexmark/flexmark-all "0.64.8"]
                 [enlive "1.1.6"]
                 [optimus "2026.05.27"]
                 [clj-rss "0.4.0"]
                 [org.clojure/data.xml "0.0.8"]]
  :aliases {"export" ["run" "-m" "bløggr.core/export"]
            "dev" ["run" "-m" "dev"]}
  :jvm-opts ["-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"]
  :profiles {:dev {:source-paths ["dev"]
                   :plugins [[lein-midje "3.2.2"]]
                   :dependencies [[midje "1.10.10" :exclusions [org.clojure/clojure]]]}})
