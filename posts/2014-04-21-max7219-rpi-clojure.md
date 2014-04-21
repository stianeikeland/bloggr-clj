{ :slug "max7219-rpi-clojure"
  :title "Max7219 LED matrix + clojure + justin bieber == true"
  :date "2014-04-21 23:00:00+01:00"
  :image "/images/max7219/header.jpg"
  :tags #{:clojure :raspberrypi :electronics}}

------

[Max7219](http://pdfserv.maximintegrated.com/en/ds/MAX7219-MAX7221.pdf) is a serially connected ([SPI](http://en.wikipedia.org/wiki/Serial_Peripheral_Interface_Bus)) LED matrix. They are quite simple devices, and you can buy them from eBay for a few bucks (including a 8x8 LED matrix display). I've bought 3, and hooked them up to the SPI-bus on a Raspberry Pi.

<figure class='half'>
    <img src="/images/max7219/max7219-led.jpg">
</figure>

Each device accept 2 byte at the time, one byte for command and one byte for data. If you send it more than 2 byte in a transmit cycle it will shift it's old values to the next device. The driver executes the command currently in memory when you end the transmit cycle. You can set one LED column (8 leds) per command, for example, to turn all LEDs in column 1 on, send the command+data pair `[1 255]`. To turn all leds in column 2 off, send `[2 0]`. You've probably guess that the LEDs represent the bits in the data byte, and you're right, sending `[3 2r11110000]` turns half the LEDs on in column 3.

I've made a small library to control chains of these drivers using Clojure. It's available on [Github](https://github.com/stianeikeland/max7219-clj) and on [Clojars](https://clojars.org/max7219). You can use it like this:

~~~ clojure
(require [max7219.core :refer :all])

; 8x8 drawing representing a space invader
(def space-invader [152 92 182 95 95 182 92 152])

; Open a connection on SPI channel 0 to 1 displays.
(def conn (open! :channel-0 1))

; Shift out bytes to the display column by column.
(set-displays! conn space-invader)
~~~

Sending to multiple displays is easy as well:

~~~ clojure
(def space-invader-1 [152 92 182 95 95 182 92 152])
(def space-invader-2 [24 220 54 63 63 54 220 24])

; Open connection to two displays
(def conn (open! :channel-0 2))

; Shift out data to two displays.
(set-displays! conn (concat space-invader-1 space-invader-2))
~~~

<figure>
    <img src="/images/max7219/anim.gif">
</figure>

The library includes a [5x7 font](https://github.com/stianeikeland/max7219-clj/blob/master/src/max7219/font.clj) and the ability to scroll text. You can use it like the following:

~~~ clojure
(def scroll-delay 30) ; in ms

(let [conn (open! :channel-0 3)]
  (scroll-text! conn "Lispy lisp lisp lisp.." scroll-delay))
~~~

Please note that scrolling blocks the thread using Thread/sleep.

If you want to do other meaningful work while scrolling text you need to do that in another thread than the one you use for scrolling. But this is 2014, and we've got [core.async](https://github.com/clojure/core.async) so that's easy. core.async is a clojure library for asynchronous programming using channels, heavily influenced by [CSP](http://en.wikipedia.org/wiki/Communicating_sequential_processes), and looks and feels almost like Go-langs go-blocks and channels. It's one of Rich Hickey's brainchilds, and consists more or less of a few giant macros that probably only two people in the world understand (or so they say).

Let's try to use it for something useful. Let's start by importing some stuff we need:

~~~ clojure
(ns twitter-scroller.core
  (:require [twitter.oauth :refer [make-oauth-creds]]
            [twitter.api.streaming :refer [statuses-filter]]
            [cheshire.core :as json]
            [max7219.core :as max7219]
            [clojure.core.async :as async :refer [go chan <! >!!]])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback))
  (:gen-class :main true))
~~~

We're going to twitter's streaming api to stream some data to our LED matrix display.

~~~ clojure
(def twitter-creds (make-oauth-creds "YABLABLBALBLABLSECRETYXe2K"
                                     "1H05NWXXXXXXXXXXXXXXSECRETXXXXXXXXXXofSGIrz2"
                                     "144553XXXXXXXXXXSECRETXXXXXXXXXXXXXXX6w1uhXKn3RkK2J"
                                     "qsmYYYYYYYYYYYYSECRETYYYYYYYYYYYYYYYYApE7qsNf"))
(def queue (chan 1000))
~~~

Set up the required oauth credentials for twitter (create a set here: [twitter apps](https://apps.twitter.com/)). And create a buffered channel for communication, buffer of 1000 items in this case.

~~~ clojure
(go
  (let [conn (max7219/open! :channel-0 3)]
    (while true
      (let [msg (<! queue)]
        (println msg)
        (max7219/scroll-text! conn msg 30)))))
~~~

Then we spawn a go-block that sets up the SPI bus (displays). And starts trying to dequeue messages from the channel. Dequeued messages are printed and displayed on the LED matrix display. This block will block when there are no messages available.

~~~ clojure
(defn queue-toot [resp msg]
  (try (->> (json/parse-string (str msg) true)
            (:text)
            (>!! queue))
       (catch Exception e)))
~~~

We define a function that parses twitter toots from json-string, extracts the text field and insert into the channel.

~~~ clojure
(statuses-filter :params {:track "bieber"}
                 :oauth-creds twitter-creds
                 :callbacks (AsyncStreamingCallback. queue-toot println println))
~~~

Now we connect to twitters status stream, filtered for toots about "bieber". An async callback uses queue-toot above to insert messages into the channel.

<figure>
    <iframe src="//player.vimeo.com/video/92539944" width="600" height="325" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe> <p><a href="http://vimeo.com/92539944">Led matrix clojure</a> from <a href="http://vimeo.com/eikeland">Stian Eikeland</a> on <a href="https://vimeo.com">Vimeo</a>.</p>
</figure>

Run this and behold - you're now streaming messages about Justin Bieber asynchronous from twitter to a SPI connected LED matrix display using clojure and core.async! Amazing! (Yes, the #clojure twitter feed was too slow..)
