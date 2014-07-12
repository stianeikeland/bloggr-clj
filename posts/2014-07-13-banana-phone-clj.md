{ :slug "clojure-banana-phone"
  :title "Banana Phones, Clojure and the Expression Problem"
  :date "2014-07-13 08:00:00+01:00"
  :image "/images/bananaphone.jpg"
  :tags #{:clojure}}

------

Clojure is a Functional First language, and I usually get by simply by passing
around native datastructures such as maps, lists and vectors. Other than doing
some simple Java interop I haven't really deep into the Object-Oriented features
of Clojure until now. But, just because a language is FF doesn't mean that it's
OO is weak sauce.

## The Banana

~~~ clojure
(ns oop.fruits)

(defprotocol Fruit
  (peel [this])
  (describe-fruit [this]))

(defrecord Banana [degree peeled?]
  Fruit
  (peel [this] (Banana. degree true))
  (describe-fruit [this]
    (str "A "
         (if peeled? "peeled" "unpeeled")
         " banana with a " degree " degrees bend")))

(defn get-a-nice-banana []
  (Banana. 30 false))
~~~

Imagine that we have a namespace called `fruits`, in this ns we have a Protocol
defined - called `Fruit` - anything that satisfied being a fruit needs
two methods - a function to peel the fruit, and a function to describe the
fruit. You can think of Protocols as being something pretty close to
"interfaces" in OO languages such as Java and C#. (They're actually more related
to Traits).

We also describe a record, a `Banana`. Think of this as a class, the banana
accepts two values - a degree saying how bent the banana is, and a bool saying
if it's peeled or not.

Records are often method-less, this one however has two methods `peel` and
`describe-fruit`, this makes the `Banana` satisfy the requirements for being a `Fruit`.

We also define a public `get-a-nice-banana` function that returns an instance of
Banana - a unpeeled 30 degree bend banana - since bananas with 30 degree bends
are the best - as we all know.

## The Apple iPhone

~~~ clojure
(ns oop.phone)

(defprotocol Phone
  (call [this number]))

(defrecord iPhone [model]
  Phone
  (call [this number]
    (str "Calling " number " using apple phone model " model "..")))

(defn call-using [phone number]
  (call phone number))

(defn get-a-nice-phone []
  (iPhone. "5s"))
~~~

Next, let's define a new namespace. This time we create a Phone protocol, phones
should be able to `call` numbers.

Then we create an iPhone, which fulfills the requirements of being a phone
since it can actually call people (not just toot and instaface and..).

We create two functions, `call-using` which allows you to call a number using a
given phone. And a convenience function for getting a nice shiny new iPhone 5s.

We now have a couple of things defined.. We have Phones and Fruits, iPhones and
Bananas. They have absolutely nothing to do with each other, and are isolated in
their own namespaces.

## Apples and Bananas - to the core

~~~ clojure
(ns oop.core
  (:require [oop.fruits :as fruit]
            [oop.phone :as phone])
  (:import [oop.fruits Banana]))
~~~

Let's start a new namespace, `oop.core`, this namespace imports the two other
namespaces we've defined.

~~~ clojure
(def iphone (phone/get-a-nice-phone))

(phone/call-using iphone 80020123)
;; => "Calling 80020123 using apple phone model 5s.."

(type iphone)
;; => oop.phone.iPhone

(satisfies? phone/Phone iphone)
;; => true
~~~

We can use the convenience function from phone to get a shiny iPhone, we see
that we can give this phone to `call-using` which then can use it to call
someone. No surprises here, as the iphone satisfies the Phone protocol.

~~~ clojure
(def banana-phone (fruit/get-a-nice-banana))

(type banana-phone)
;; => oop.fruits.Banana

(satisfies? fruit/Fruit banana-phone)
;; => true

(fruit/describe-fruit banana-phone)
;; => "A unpeeled banana with a 30 degrees bend"

(fruit/describe-fruit (fruit/peel banana-phone))
;; => "A peeled banana with a 30 degrees bend"
~~~

Let's try to create a banana, it's a nice banana of the type
`oop.fruits.Banana`, it satisfies the `Fruit` protocol from the `oop.fruits`
namespace. We can call describe-fruit on it, and we can peel it. No surprises
here either.

Now, what if we could actually take our banana and call someone? Wouldn't that
be wonderful? But alas..

~~~ clojure
(phone/call-using banana-phone 80020123)
;; Booom exception.. no implementation of oop.phone/Phone for class oop.fruits.Banana

(satisfies? phone/Phone banana-phone)
;; => false
~~~

The banana blows up, spewing yellow goo all over our system - it turns out that a `Banana` isn't actually a `Phone`. That makes me a very sad panda :( I've always wanted a real banana phone.

<figure>
<a href="/images/sad-panda.jpg"><img src="/images/sad-panda.jpg"></a>
</figure>

In a regular OO language like Java or C#, I would probably attack this by encapsulating
a banana instance in a new class, one that also satisfies the Phone interface.

But no worries, watch this:

~~~ clojure
(extend-type Banana
  phone/Phone
  (call [this number]
    (str "Calling " number " using a " (:degree this) " degrees bent banana.. WTF?")))
~~~

Still in the oop.core namespace, we extend the `oop.fruits/Banana` type to also
satisfy the `oop.phone/Phone` protocol.

~~~ clojure
(type (fruit/get-a-nice-banana))
;; => oop.fruits.Banana
~~~

Our banana is still just of type Banana.

~~~ clojure
(satisfies? phone/Phone (fruit/get-a-nice-banana))
;; => true
~~~

A `Banana` is a `Phone`, it actually satisfies the `Phone` Protocol... and..

~~~ clojure
(phone/call-using (fruit/get-a-nice-banana) 80020123)
;; => "Calling 80020123 using a 30 degrees bent banana.. WTF?"
~~~

Holy cow! We can now not only call using the Apple iPhone 5s, but also using the
30 degrees bent Banana! And that's using a fresh banana we just got from the
`oop.fruits` namespace - we didn't monkey patch it or modify it. We didn't
change the `oop.fruits` namespace or the `oop.phone` namespace, we just passed it along to
`oop.phone`.

Mind blow, that's a really elegant solution to the [expression problem](http://en.wikipedia.org/wiki/Expression_problem) if you ask me..

<figure>
<iframe width="620" height="470" src="//www.youtube.com/embed/j5C6X9vOEkU" frameborder="0" allowfullscreen> </iframe>
</figure>

> Ring ring ring ring ring ring ring bananaphone.

> Ping pong ping pong ping pong ping pananaphone.


## Bonus feature

As a bonus, here's another pretty nice feature called `reify`. Reification means
to make something real, bring it into being or make something concrete.

In clojure using `reify` you can actually implement an interface (protocol)
anonymously. Observe:

~~~ clojure
(def anon-phone (reify phone/Phone
                  (call [this number]
                    (str "Calling " number " anonymously.."))))

(phone/call-using anon-phone 80020123)
;; => "Calling 80020123 anonymously.."
~~~

An anonymous implementation of the `Phone` protocol. Try doing that in C# /
Java! (Håvard tells me you can do it in F#). Maybe there's something to the saying that "Clojure is a better Java than
Java" after all?
