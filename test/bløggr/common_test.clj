(ns bløggr.common-test
  (:use midje.sweet)
  (:require [bløggr.common :refer :all]
            [clj-time.core :as t]))

(def source-code-with-lang "```bash\nls -la\necho $HEI\n```")
(def rendered-with-lang "<pre><code class=\"bash\"><div class=\"highlight\"><pre>ls -la\n<span class=\"nb\">echo</span> <span class=\"nv\">$HEI</span>\n</pre></div>\n</code></pre>")
(def source-code-without-lang "```\nls -la\necho $HEI\n```")
(def rendered-without-lang "<pre><code>ls -la\necho $HEI\n</code></pre>")

(def blogdate (t/from-time-zone (t/date-time 2007 8 28 1 59 36)
                                (t/time-zone-for-offset 0)))

(fact "parse-datestring parses string to date"
      (parse-datestring "2007-08-28 01:59:36+00:00") => blogdate)

(fact "highlights source code with lang set"
      (:body (-> {:body source-code-with-lang}
                 (markdown)
                 (enliveify)
                 (highlight)
                 (render))) => rendered-with-lang)

(fact "skips highlighting code when lang not set"
      (:body (-> {:body source-code-without-lang}
                 (markdown)
                 (enliveify)
                 (highlight)
                 (render))) => rendered-without-lang)

(fact "enliveify turns post into enlive data"
      (-> {:body "<div>brille</div>"}
          enliveify :body first :content first) => "brille")

(fact "render turns enlive post into html post"
      (let [html "<div>brille</div>"]
        (-> {:body html}
            enliveify
            render
            :body) => html))

(fact "strip-comments strips comments from html"
  (strip-comments "Hello <!-- Bah -->World!") => "Hello World!")
