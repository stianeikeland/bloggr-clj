{ :slug "pid-transducer"
:title "Boiling Sous-Vide Eggs using Clojure's Transducers"
:date "2014-10-06 08:00:00+0100"
:image "/images/2014-cooking-transducers/banner.jpg"
:tags #{:clojure :sousvide}}

------

I love cooking, especially geeky molecular gastronomy cooking, you know, the
type of cooking involving scientific knowledge, -equipment and ingredients like
[liquid nitrogen][liquid] and similar. I already have a sous-vide setup, well,
two actually (here is one of them: [sousvide-o-mator][sousvide]), but I have
none that run Clojure. So join me while I attempt to cook up some sous-vide eggs
using the new transducers coming in Clojure 1.7. If you don't know what
transducers are about, take a look [here][transducers] before you continue.

[liquid]: /2013/02/04/liquid-nitrogen-hop-ice-cream/
[sousvide]: /2011/08/17/sousvide-o-mator/
[transducers]: /2014/08/14/transducers/

To cook sous-vide we need to keep the temperature at a given point over time.
For eggs, around 65C is pretty good. To do this we use a PID-controller.

~~~ clojure
(defrecord Pid [set-point k-p k-i k-d error-sum error-last output-max output])

(defn make-pid
  "Create a new PID-controller.
   Requires: target temperature, kp, ki, kd gain.
   Optional: output-max=100 (error-sum=0, error-last=0, output=0)"
  [set-point k-p k-i k-d
   & {:keys [error-sum error-last output-max output]
      :or   {error-sum 0 error-last 0 output-max 100 output 0}}]
  (Pid. set-point k-p k-i k-d error-sum error-last output-max output))

(defn calculate-pid
  "Calculate next PID iteration"
  [{:keys [set-point error-last error-sum k-p k-i k-d output-max] :as pid} input]
  (let [error     (- set-point input)
        error-dv  (- error error-last)
        error-sum (+ error-sum error)
        output    (min output-max
                       (+ (* k-p error)
                          (* k-i error-sum)
                          (* k-d error-dv)))]
    (assoc pid :error-last error :error-sum error-sum :output output)))
~~~

Let's start by creating a record type for our PID-controller. The PID algorithm
requires a few values: `set-point` - the target temperature, example: 65C for a
perfect sous-vide egg. `k-p` - the proportional gain, `k-i` - the integral gain
and `k-d` - the derivative gain. The proportinal gain is the most important part
of the Algorithm, it controls how hard we push the pedal to the floor depending
on the current error (distance to target-temperature). The integral factor looks
at error over time. It's what tries to keep the output steady when
we've reached our target. The derivative factor tries to counteract overshooting
by looking at the error derivative (the change in error rate) - it dampens the
other factors when we close on our target.

We also need to keep track of `error-sum`, the previous error - `error-last`,
and we need a place to put the recommended output.

Next, we create `make-pid` - a constructing function, this simply sets a few
default values if they're not provided.

Then we implement the PID algorithm as a function - `calculate-pid`. It accepts a
PID-controller and the current input sample (ex: temperature). It returns the
PID-controller record for the next iteration.

~~~ clojure
(defn pid-transducer [set-point k-p k-i k-d]
  (fn [xf]
    (let [pid (volatile! (make-pid set-point k-p k-i k-d))]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
           (vswap! pid (fn [p] (calculate-pid p input)))
           (xf result (:output @pid)))))))
~~~

We can then use this to create a "stateful" transducer for doing PID operations
on an input (sequence, channel, stream, etc..). (Clarification from Alex Miller:
The transducer isn't stateful, but the returning reducing function is, ofc -
thanks!) Transducers look pretty weird (mostly because of their nested
multi-arity lambdas), but they smell pretty nice, so I guess they're ok.

To help keep state in a transducer reducing function, clojure 1.7 introduces
volatiles - volatiles work just like atoms - but with some limitations (rely on
thread isolation for compound atomic swap! operations, etc). They are faster
than atoms, but otherwise work just the same. If you're used to atoms, their use
should be pretty self explanatory.

All values received by the transducer is given to the PID-controller. The
PID-controller state is kept in a volatile, and updated for every iteration.

~~~ clojure
(into [] (pid-transducer 100 0.2 0.05 0)
      [0 1 3 6 10 20 31 45 61 81 91 98 100 105 110 120 110 100])
; [5.0 9.95 14.8 19.5 24.0 28.0 31.45 34.2 36.15 37.1 37.55 37.65 37.65 37.4 36.9 35.9 35.4 35.4]
~~~

Let's test it out by pretend we're trying to make something reach 100C, we give
the PID-controller a fake sequence of measured temperatures over time, and
transduce into a vector. The output describes how hard the PID-algorithm (with
these settings) would have pushed on the throttle if the system responded like
the input temperature sequence.

The great thing about transducers is that they're general implementations of
operations over data, they really do not care how the data arrives or how it
should be delivered. It works with data structures, streams, channels, and so
on.

So, imagine that we have an electric kettle. We can read the temperature of this
kettle and we can control the heating element. We receive temperatures on a
channel, and can push instructions to the heating element on another channel. If
we have this setup, then we can simply plug in our PID transducer in the middle,
and everything should work!

<figure>
<a href="/images/2014-cooking-transducers/diagram.svg">
<img src="/images/2014-cooking-transducers/diagram.svg" onerror="this.src='/images/2014-cooking-transducers/diagram.png'">
</a>
</figure>

~~~ clojure
;; Temperatures arrive via this channel
(def temperatures (chan))

;; This channel accepts temperatures, and supplies
;; PID outputs, trying to achieve a temperature of 65C
(def pid-output (chan 1 (pid-transducer 65 0.1 0.02 0.01)))

;; This channel is used to ask the kettle for the next
;; temperature sample (once our PID cycle is done.
(def fetch-next (chan))

;; We pipe temperatures into the pid-controller:
(pipe temperatures pid-output)
~~~

Next we create a few channels, one for temperatures, one for pid outputs, and
one to ask for new temperature samples. We connect the temperatures to the pid channel.

~~~ clojure
(defn control-heater [pid-output fetch-next]
  (go-loop []
    (when-let [pid-time (<! pid-output)]
      (let [time-on  (int (* 300 pid-time))
            time-off (int (- 30000 time-on))]
        (when (< 0 time-on)
          (heater-on!)
          (<! (timeout time-on))
          (heater-off!))
        (<! (timeout time-off))
        (>! fetch-next :next)
        (recur)))))

(control-heater pid-output fetch-next)
~~~

The above function will wait for a value from the PID-controller, calculate how
long (ms) the power needs to be on, and how long (ms) it needs to be off in the
next cycle. It then turns the heater on, waits a bit, turns it off, waits a bit,
and requests the next value.

<figure class="half">
<a href="/images/2014-cooking-transducers/kettle.jpg"><img
src="/images/2014-cooking-transducers/kettle.jpg"></a>
<a href="/images/2014-cooking-transducers/power.jpg"><img
src="/images/2014-cooking-transducers/power.jpg"></a>
</figure>


~~~ clojure
(zmq-send! conn {:system "power"
                 :msgtype "command"
                 :location "kitchen-water"
                 :command "on"}
~~~

In my case, the `heater-on!` and `heater-off!` functions simply send a message
to my zeromq based home automation system. I already have power control of my
water kettle and my coffee cooker - so all that's required is simply a
JSON-formatted message on a message bus. Another way to do this would for
example be an arduino with a solid state relay. See [intro][homeauto1],
[sensors][homeauto2] and [power][homeauto3] for more details of my home
automation setup.

[homeauto1]: /2012/07/08/building-a-home-automation-system-part-1-intro/
[homeauto2]: /2012/09/24/building-a-home-automation-system-the-broker-and-sensors-part-2/
[homeauto3]: /2012/10/20/building-a-home-automation-system-power-control-part-3/

~~~ clojure
;; Open serial port
(def port (serial/open "/dev/tty.usbserial" 115200))

;; Put values into the temperatures channel
(serial/on-value port (partial >!! temperatures))

;; Ask arduino for next temperature when we're ready for one..
(go-loop [_ (<! fetch-next)]
  (serial/write-str "next")
  (recur (<! fetch-next)))
~~~

The input temperatures are received over the serial port from an arduino with a
DS18B20 temperature probe.

<figure>
<a href="/images/2014-cooking-transducers/egg.jpg"><img src="/images/2014-cooking-transducers/egg.jpg"></a>
</figure>

Start the system by running: `(>!! fetch-next :next)`, allow the system to
stabilize, drop in a couple of eggs and wait 45 minutes. You should be awarded
with the most perfect creamy egg yolk you have ever tasted. I like mine on a
piece of bread with avocado and a sprinkle of salt. Enjoy, now you're cooking with clojure!
