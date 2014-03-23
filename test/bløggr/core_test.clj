(ns bløggr.core-test
  (:use midje.sweet)
  (:require [bløggr.core :refer :all]
            [clj-time.core :as t]))

(def blog-post "
  {
  :slug \"openwrt-on-hama-mpr-a1-v2-2\"
  :title \"OpenWRT on Hama MPR-A1 (v2.2)\"
  :image \"2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg\"
  :date \"2007-08-28 01:59:36+00:00\"
  :tags #{:hacking :mpr-a1 :openwrt :router}
  }

------
body content")

(def source-code-with-lang "```bash\nls -la\necho $HEI\n```")
(def rendered-with-lang "<pre><code class=\"bash\"><div class=\"highlight\"><pre>ls -la\n<span class=\"nb\">echo</span> <span class=\"nv\">$HEI</span>\n</pre></div>\n</code></pre>")
(def source-code-without-lang "```\nls -la\necho $HEI\n```")
(def rendered-without-lang "<pre><code>ls -la\necho $HEI\n</code></pre>")

(def blogdate (t/from-time-zone (t/date-time 2007 8 28 1 59 36)
                                (t/time-zone-for-offset 0)))

(fact "parse-datestring parses string to date"
      (parse-datestring "2007-08-28 01:59:36+00:00") => blogdate)

(fact "parse-post extracts header from blog post"
      (:header (parse-post blog-post)) => {:image "2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg"
                                           :slug "openwrt-on-hama-mpr-a1-v2-2"
                                           :tags #{:hacking :mpr-a1 :openwrt :router}
                                           :date blogdate
                                           :title "OpenWRT on Hama MPR-A1 (v2.2)"})

(fact "parse-post extracts body from blog post"
      (:body (parse-post blog-post)) => "body content")

(fact "filename-body-map turns a post into title => body map"
      (filename-body-map {:header {:slug "really-cool-post" :date blogdate} :body "post body"}) =>
      {"/2007/08/28/really-cool-post/index.html" "post body"})


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

(fact "apply-post-layout should apply post template to post"
      (let [content (:body
                     (apply-post-layout {:body "this is the body"
                                         :header {:title "post title"
                                                  :date blogdate}}))]
        content => (contains "this is the body")
        content => (contains "<h1 id=\"article-title\">post title</h1>")
        content => (contains "<time datetime=\"2007-08-28T01:59:36Z\" id=\"post-timestamp\">Tue, 28 Aug 2007 01:59</time>")))
