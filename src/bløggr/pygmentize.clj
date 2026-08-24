(ns bløggr.pygmentize
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(def ^:private cache (atom {}))
(def ^:private style-cache (atom nil))

(defn- run [& args]
  (try
    (apply shell/sh "pygmentize" args)
    (catch java.io.IOException e
      (throw (ex-info "pygmentize not found on PATH - required for syntax highlighting"
                      {:cmd "pygmentize"}
                      e)))))

(defn stylesheet
  "Token-coloring rules from the installed pygmentize, .highlight-scoped."
  []
  (if-let [cached @style-cache]
    cached
    (let [{:keys [out exit]} (run "-S" "default" "-f" "html" "-a" ".highlight")
          css (if (zero? exit)
                (->> (str/split-lines out)
                     (filter #(str/starts-with? % ".highlight ."))
                     (str/join "\n"))
                "")]
      (reset! style-cache css)
      css)))

(defn highlight
  "Returns highlighted HTML, nil for an unknown lexer."
  [code lang]
  (let [key [lang code]]
    (or (get @cache key)
        (let [{:keys [out exit]} (run "-f" "html" "-l" lang :in code)]
          (when (zero? exit)
            (let [result (str/trim-newline out)]
              (swap! cache assoc key result)
              result))))))