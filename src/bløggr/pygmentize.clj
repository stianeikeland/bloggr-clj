(ns bløggr.pygmentize
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(def ^:private cache (atom {}))
(def ^:private style-cache (atom nil))

(def ^:private light-style "default")
(def ^:private dark-style "one-dark")

(defn- run [& args]
  (try
    (apply shell/sh "pygmentize" args)
    (catch java.io.IOException e
      (throw (ex-info "pygmentize not found on PATH - required for syntax highlighting"
                      {:cmd "pygmentize"}
                      e)))))

(defn- token-css
  "Token-coloring rules from the given pygmentize style, .highlight-scoped."
  [style]
  (let [{:keys [out exit]} (run "-S" style "-f" "html" "-a" ".highlight")]
    (if (zero? exit)
      (->> (str/split-lines out)
           (filter #(str/starts-with? % ".highlight ."))
           (str/join "\n"))
      "")))

(defn stylesheet
  "Token-coloring rules from the installed pygmentize, .highlight-scoped:
   light palette, then the dark palette under prefers-color-scheme: dark."
  []
  (or @style-cache
      (let [css (str (token-css light-style)
                     "\n\n@media (prefers-color-scheme: dark) {\n"
                     (token-css dark-style)
                     "\n}\n")]
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