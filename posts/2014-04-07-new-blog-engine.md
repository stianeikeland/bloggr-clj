{ :slug "clojure-blog-engine"
  :title "Clojure blog engine!"
  :date "2014-04-07 22:00:00+00:00"
  :image "/images/2014-04-07-new-blog-engine/header.png"
  :tags #{:blog :clojure}}

------

I've had a few times where I've tried to blog lately, but instead ending up
closing the blog post in anger because of some stupid wordpress issue.
Some times it would destroy my markup when trying to switch between
visual mode and text mode, other times just plain stupid errors. I've
been using a hosted wordpress.com blog for many years now, mostly out
of convenience - but once the tools work against me I guess it's time
to pack my bag and move on.

### Static blog pages on S3 ###

So I've jumped on the generated static page blog bandwagon, static
pages hosted on Amazon S3 in this case. I've thought about doing this
a few times, looking a bit at [jekyll](http://jekyllrb.com/) and
similar generators.

A few months ago Magnar Sveen released
[Statis](https://github.com/magnars/stasis). A minimalistic Clojure library for
generating static pages. I tried it out and decided, hey, this is
pretty cool, I can use this, and probably also learn a bit of Clojure
in the process (been doing Clojure on and off for the last year or
so.)

It's taken some time, I've been pretty busy lately and have only been able to
put in a few hours in the evenings now and then - but, it's
ready! Or at least ready enough to crank out a few initial pages.
There's still lots to do, but, release early and iterate I guess :)

The code is over at Github if you're interested:
[Bløggr-clj](https://github.com/stianeikeland/bloggr-clj).

### Loading, serving and exporting ###

I'm using [Statis](https://github.com/magnars/stasis) for loading
assets (posts written in markdown) and exporting final pages. It also
hooks into [Ring](https://github.com/ring-clojure/ring) (the main
web-application library for clojure), allowing you to easily serve pages locally while testing.

With stasis you can do thing like the following, load a set of assets,
do whatever processing you want (turn markdown into html, syntax
highlight code block, add templates, etc, etc) and then either export
it as files or serve it up using Ring.

~~~ clojure
(-> (stasis/slurp-directory "posts/" #"\.md$")
    (post/markdown)
    (post/syntax-highlight-code)
    (post/apply-template)
    (stasis/export-pages "export/"))
~~~

### Markdown ###

For markdown I'm using [Cegdown](https://github.com/Raynes/cegdown),
which is simply a clojure wrapper for
[Pegdown](https://github.com/sirthias/pegdown) - a pure java markdown
processor made using the PEG parser library. I tried a few different
markdown processors, but this was the only one that managed to chew
trough my blog-posts without too many hickups. Some of the others had
trouble dealing with inline html in some corner cases, etc.

Now I write my posts using markdown, with a Clojure Edn header for
metadata.

~~~ clojure
{:slug "clojure-blog-engine"
 :title "Clojure blog engine!"
 :date "2014-04-07 22:00:00+00:00"
 :image "/images/2014-04-07-new-blog-engine/header.png"
 :tags #{:blog :clojure}}

**Blablabla** markdown *bla*

1. markdown
2. more markdown
~~~

### Syntax highlighting ###

For syntax highlighting I use
[Clygments](https://github.com/bfontaine/clygments). Which is a
wrapper around the good old pygments highlighter you've probably seen
hundreds of times (I believe Github uses it.). By wrapping a piece of
code in a few tildes (and a language hint) it provides fast and easy
highlighting for most languages out there - even for languages as old
and awesome as SNOBOL4.

~~~ snobol
ASK
   OUTPUT = "Your name? "
   NAME = INPUT           :F(DONE)
   OUTPUT = "Hello " NAME :(ASK)
DONE
   OUTPUT = "Finished"
END
~~~

### Templating ###

For templating I were stuck between
[Hiccup](https://github.com/weavejester/hiccup) (love the name!),
[Enlive](https://github.com/cgrand/enlive) and
[Selmer](https://github.com/yogthos/Selmer). I ended up using Enlive,
simply because I had a design I wanted to use that was already in
HTML and also wanted to massage data using clojure data structures
instead of a custom DSL. I have to say, Enlive isn't the easiest to get
your head around (at least it wasn't for me), but David Nolan has a
decent [tutorial](https://github.com/swannodette/enlive-tutorial/)
over on github.

~~~ clojure
(enlive/deftemplate post "layout/post.html"
  [post]
  [:div#title] (enlive/content (:title post))
  [:div#content] (enlive/html-content (:content post)))
~~~

Using enlive you can do things like creating templates, and do
selecting based on css-like selectors. It's also pretty good for
scraping as you probably can guess.

### RSS ###

RSS generation is done using
[clj-rss](https://github.com/yogthos/clj-rss), a simple and small
library for spitting out some rss-style XML.

``` clojure
(channel-xml {:title "Channel Title" :link "http://foo.no" :description "Channel desc"}
             {:title "Post title" :link "http://foo.no/1" :description "Content" :author "a@b.no"}
             {:title "Another post" :link "http://foo.no/2" :description "More content" :author "a@b.no"})
```

### Conclusion ###

I really like clojure these days, this blog generator has been
so fun to code. There are still some things to do, like
generating a sitemap, tag overviews, better navigation and
presentation of posts (front page sucks, etc.). But I'm planning to
get those ironed out in the coming weeks.

I also want to change the URL of the blog, from
[blagg.tadkom.net](http://blagg.tadkom.net) to something on the
[eikeland.se](http://eikeland.se)-domain. But do not want to risk
changing everything at once.

On a related note, I'm considering paying for a ticket to [Euroclojure](http://euroclojure.com/2014/)
this summer. Previously I've only gone to free conferences, such as
[FOSDEM](https://fosdem.org/2014/), since I've never worked at a place
that had the resources to send me (or my collegues). This year I was
really lucky and won a NDC-ticket, looking forward to that :)
Euroclojure is pretty cheap, and in Poland as well, so heavily considering it.
Would be awesome to hear a talk from Hickey and Nolan live, and the propolsals
inbox from other speakers is looking promising! Also looking at
Strangeloop, that would be totally awesome, but flight to a
US-conference is pretty expensive.
