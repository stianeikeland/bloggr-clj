(defproject bløggr "0.1.0-SNAPSHOT"
  :description "Blog engine for eikeland.se"
  :url "http://eikeland.se/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [stasis "1.0.0"]
                 [ring "1.2.2"]
                 [me.raynes/cegdown "0.1.1"]
                 [clygments "0.1.1"]
                 [enlive "1.1.5"]
                 [optimus "0.14.2"]]
  :ring {:handler bløggr.core/ring}
  :profiles {:dev {:plugins [[lein-ring "0.8.10"]
                             [lein-midje "3.1.3"]]
                   :dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]]}})
