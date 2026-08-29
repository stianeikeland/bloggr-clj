(ns bløggr.common-test
  (:use midje.sweet)
  (:require [bløggr.common :refer :all]))

(def source-code-with-lang "```bash\nls -la\necho $HEI\n```")
(def rendered-with-lang "<div class=\"highlight\"><pre><span></span>ls<span class=\"w\"> </span>-la\n<span class=\"nb\">echo</span><span class=\"w\"> </span><span class=\"nv\">$HEI</span>\n</pre></div>\n")
(def source-code-without-lang "```\nls -la\necho $HEI\n```")
(def rendered-without-lang "<pre><code>ls -la\necho $HEI\n</code></pre>\n")

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
