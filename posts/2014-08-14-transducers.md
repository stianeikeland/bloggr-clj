{ :slug "transducers"
:title "Transduce me up, Hickey!"
:date "2014-08-14 08:00:00+0100"
:image "/images/beam-me-up.jpg"
:tags #{:clojure}}

------

**Transducers** (reducing function transformers) are apparently coming to Clojure in v1.7 - but what is a
 transducer? Are they some sort of a [continuum transfunctioner][ct]? Some say they are [perverse lenses][lens], others that they are [monoidals][monoidal]. I have no idea, but I want to find out! So let's figure out what's changed and play with them for a bit.

[Here's][commit] the work in progress commit that added them to clojure.core. A few signatures were changed, and a few
functions were added. In particular, it seems like most of the higher order functions that works on top of the sequence abstractions were
altered - map, filter, reduce + friends. So, what's the new signature of map?

~~~
clojure.core/map
([f] [f coll] [f c1 c2] [f c1 c2 c3] [f c1 c2 c3 & colls])
Added in 1.0
  Returns a lazy sequence consisting of the result of applying f to
  the set of first items of each coll, followed by applying f to the
  set of second items in each coll, until any one of the colls is
  exhausted.  Any remaining items in other colls are ignored. Function
  f should accept number-of-colls arguments. Returns a transducer when
  no collection is provided.
~~~

Okay! The first signature here is new. If you apply map to a function - and NO collection, it will return a transducer.

~~~ clojure
(def increment (map inc))
~~~

What do we actually have now? Is this simply partial function application (partial map inc) or currying or something
like that? Let's naively give it a collection and see what happens.

~~~ clojure
(increment [1 2 3 4 5]) ;; Don't use them like this..
#<core$map$fn__4338$fn__4339 clojure.core$map$fn__4338$fn__4339@2e2f4944>
~~~

Hmm, no, this is defiantly not partial application of map, we get a function in return here, so I'm pretty sure
this is not how they should be used. Let's look at how you are supposed to use them.

~~~ clojure
;; We can get a sequence of something via a transducer function.
(sequence increment [1 2 3 4 5])
;; => (2 3 4 5 6)

;; We can transduce into a collection:
(into [] increment [1 2 3 4 5])
;; => [2 3 4 5 6]

;; We can transform and reduce a collection:
(transduce increment + [1 2 3 4 5])
;; => 20
~~~

But.. I can already do all of these steps using the existing sequence abstractions.. Example:

~~~ clojure
(reduce + (map inc [1 2 3 4 5]))
;; => 20
~~~

Transducers got one nice trick up their sleeve, they have a really nice composability.

~~~ clojure
;; Transducer functions:
(def add-2 (map (partial + 2)))
(def keep-odd (filter odd?))
(def square (map #(* % %)))

;; Composing multiple transducers
(def add-2-keep-odd-squared (comp add-2
                                  keep-odd
                                  square))

(sequence add-2-keep-odd-squared (range 1 100))
;; => (9 25 49 81 121 169 225 289 361 441 529 625 729 841 961 1089 ....
~~~

But.. similar stuff can already be done using existing functionality..

~~~ clojure
(->> (range 1 100)
     (map (partial + 2))
     (filter odd?)
     (map #(* % %)))
;; => (9 25 49 81 121 169 225 289 361 441 529 625 729 841 961 1089 ....
~~~

So, what's the point here really? I'm going to assume that there is a really good point, since the people creating this stuff are way
smarter than me.. So let's dig deeper. Why couldn't we use the transducers directly earlier? Let's have a look at the
signatures of these functions:

~~~
map f: (a->b)->(x->b->x)->(x->a->x)

filter pred: (a->bool)->(x->a->x)->(x->a->x)

flatmap f: (a->[b])->(x->b->x)->(x->a->x)
~~~

Aha, that's a bit clearer. Filter might be the easiest to look at, it takes a predicate and returns a
transducer function. The transducer itself has signature `(x->b->x)->(x->a->x)` where `a = b` for filter. It takes another function that
takes something and an item type b, and returns something. The result of this again is another function that takes
something and item a.. and output something. However, I still find this a bit confusing.

It might be easier if we have a look at what x might be here: collections, sequences, and so on. So, for a vector, I need
to give a transducer a function that operates on a vector and on an item, and outputs a vector. Let's try to do that!

~~~ clojure
;; let's call the x->b->x part "builder"..
;; and the x->a->x "applier".. for a lack of better names.

;; Filtering transducer, remove even numbers:
(def keep-odd (filter odd?))

;; A simple vector builder, takes x and b, returns x.
(defn builder [v b]
  (vec (conj v b)))

;; Now, let's give the builder to the transducer..
(def applier (keep-odd builder))
~~~

We now have access to the inner function with signature `x->a->x`, let's try to use it to filter a list of numbers,
manually. It takes a something, and an item. We don't have this something yet, so let's give it nil, and let's give it
the first number from our list as b.

~~~ clojure
;; Vector of numbers: [1 2 3 4 5]
(applier nil 1)
;; => [1]
(applier [1] 2)
;; => [1]
(applier [1] 3)
;; => [1 3]
(applier [1 3] 4)
;; => [1 3]
(applier [1 3] 5)
;; => [1 3 5]
~~~

Do you see what's going on? We are driving the vector collection logic here (deconstruction and construction). The
transducer itself has zero knowledge about the collection it's working on!

## Why is this a good thing?

In clojure there are a couple of different abstractions over collections of data. We have the sequence abstraction,
allowing us to work on lazy sequences of data using operations like map, filter, reduce, take, etc - all chilled out
working lazily hammock-style. Then we have reducers, a highly efficient (potentially concurrent/parallel) way of operating on
collections through r/map, r/fold, r/filter, r/flatten, r/reduce, etc. We also have core.async, which work by shuffling data
through channels (CSP-style), core.async has functions like map<, map>, filter<, filter>.

Do you see a pattern? Yes? Cool! Let's play with core.async a bit. The latest development-version of core.async that is.
Let's look up the documentation for `chan`

~~~
clojure.core.async/chan
([] [buf-or-n] [buf-or-n xform] [buf-or-n xform ex-handler])
  Creates a channel with an optional buffer, an optional transducer
  (like (map f), (filter p) etc or a composition thereof), and an
  optional exception-handler.  If buf-or-n is a number, will create
  and use a fixed buffer of that size. If a transducer is supplied a
  buffer must be specified. ex-handler must be a fn of one argument -
  if an exception occurs during transformation it will be called with
  the Throwable as an argument, and any non-nil return value will be
  placed in the channel.
~~~

Hey! there's an optional transducer here now.

~~~ clojure
(require '[clojure.core.async :as async :refer [chan onto-chan <!!]])

;; Create a channel, buffer size 10, with a transducer added
(def c (chan 10 add-2-keep-odd-square))

;; Dump all the numbers from 1 to 100 into the channel (this runs in a go-block)
(onto-chan c (range 1 100))

;; Pull all the transformed numbers out of the channel, into a vector.
(<!! (async/into [] c))
;; => [9 25 49 81 121 169 225 289 361 441 529 625 729 841 961 1089 .... ]
~~~

Before transducers we would have had to express the add-2-keep-odd-square logic using core async's higher order functions, but now
we can instead reuse the transducer we've already created. Here the transducer is used to map and filter over something
that isn't even a collection/sequence. The transducer has no idea that it's handling data on a core.async channel, we
didn't have to change the map/filter/etc logic in any way to make it work on channels instead of sequences.

This means that all the code for higher order convenience functions in core.async (map<>, mapcat<>, filter<>, etc) now
basically is deprecated - and it has now been labeled as such in the documentation. Boom!

## Conclusion

Transducers makes it so that that the logic you express using filter, map, reduce, take, random-sample, partition-by, etc, etc, etc, can
be re-used in completely different contexts. The logic is all isolated, transducers are oblivious to the underlying collection,
stream, observable, iterateable, whatever context. That's an useful abstraction if you ask me :)

**For an example of how to make a transducer, head over to:
[Boiling Sous-Vide Eggs using Clojure's Transducers][eggs].**

[eggs]: /2014/10/06/pid-transducer/
[lens]: http://www.reddit.com/r/haskell/comments/2cv6l4/clojures_transducers_are_perverse_lenses/
[monoidal]: http://oleksandrmanzyuk.wordpress.com/2014/08/09/transducers-are-monoid-homomorphisms/
[commit]: https://github.com/clojure/clojure/commit/2a09172e0c3285ccdf79d1dc4d399d190678b670
[ct]: http://nb.urbandictionary.com/define.php?term=continuum%20transfunctioner
