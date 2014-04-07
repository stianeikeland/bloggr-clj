{ :slug "sousvide-o-mator"
  :title "SousVide-O-Mator"
  :date "2011-08-17 14:27:00+00:00"
  :tags #{:electronics :food :sousvide :arduino}}

------

<figure>
  <img src="/images/2011-08-17-sousvide-o-mator/k7im7155.jpg" alt="">
</figure>

Sous Vide (french for under vacuum) is a cooking technique where food is sealed in airtight plastic bags and submerged in a water bath held at a specific temperature. Cooking sous vide usually takes a long time, but it has the advantage that the food is cooked perfectly evenly, you are able to heat a steak to the exact same temperature through out the entire piece of meat. A cut through a normal steak cooked on a pan usually reveals a gradient going from brown/gray to pink/red in the middle - less heat penetrate to the middle, so when the inside is medium rare the outside is usually well done++. A cut through a sous vide cooked steak looks more like the above - constant color and consistency through out the entire steak (the steak is normally finished (very quickly) in a frying pan to caramelize and kill any surface bacteria).

The result tastes really great, and it's hard to mess up - you can leave a steak in the water bath for hours without any problems. Cooking in a bag preserves so much of the moisture in the meat, and the long cooking time breaks down a lot of the connective tissue - leaving you with a perfectly cooked steak that is super-moist and tender enough to eat with only a fork.

<figure>
  <img src="/images/2011-08-17-sousvide-o-mator/5252570920_0bfb5753ef.jpg" alt="">
  <figcaption>
    Photo by FotoosVanRobin - CC BY-SA 2.0
  </figcaption>
</figure>

A professional sous vide setup costs at least >$1000, so it's a bit out of reach for the normal home cook - except for the DIYers.. It's not that hard to build yourself if you put your mind to it. What you need is the following components:

  1. Water bath with a electric heater.
  2. Some method of circulating the water.
  3. A way of accurately regulate the heater based on water temperature
  4. Some way of plastic bag packing you meat.

Water bath with heater is easy enough, there are tons of items out there that does this - slow cookers and rice cookers for example. I use a simple rice cooker, the cheaper/simpler the better (we're going to cycle it's power on/off, a dumb cooker will behave better facing a power loss). To circulate the water I use a simple ebay aquarium pump (payed [$9.90](http://cgi.ebay.com/ws/eBayISAPI.dll?ViewItem&item=180641032508) for mine). To pack the meat in airtight bags you can either buy a cheap vacuum-packer or simply use zip-lock bags (fill your sink with water, add meat to bag, submerge bag in water but keep the opening above waterlevel - pressure from the water will press out all the air, seal the bag..)

That's the easy part, the hard part is regulating the heater to accurately hit a specific temperature. A simple thermostat won't do, because of the long dead time in such a system - you end up with a cycle overshooting and undershooting the target temperature - this is a job for a real PID controller (**proportional-integral-derivative controller**).

![pid formula](/images/2011-08-17-sousvide-o-mator/pid-formula.png)

A PID-controller monitors a system and tries to bring it to a specific state by providing a output value. For example, monitors the temperature of a water bath and tries to bring it to exactly 60 degrees celcius by regulating a heater. The forumula consists of three terms, first the gain - how hard to press the pedel to accelerate to a certain speed based on the current speed. The second part is an integral, looking back, how fast where we changing when we applied this much pressure to the pedal in the past.. Third term is a derivative trying to predict the future, when do I need to stop accelerating to be able to make the next turn.

Implementing such a formula seems straight forward and only requires a few lines of codes - but there are surprisingly many things to concider and special cases to deal with when implementing it - read more about it at [Brett Beuregard's](http://brettbeauregard.com/blog/2011/04/improving-the-beginners-pid-introduction/) blog. Brett has made a nice library for atmel microcontrollers (arduino based), you can find it here: [PID v1](http://code.google.com/p/arduino-pid-library/).

So what I have done is to make a microcontroller based PID controller. It's based on an Atmega 328p chip running the arduino bootloader, it has three buttons and an 4x20 character LCD to handle user interaction. And a DS18B20 one wire temperature sensor to meassure the temperature of my water bath and a SSR (Solid State Relay) to cycle the power of the rice cooker on and off.

<figure>
  <img src="/images/2011-08-17-sousvide-o-mator/K7IM7131-2-2.jpg" alt="">
</figure>

It's a fairly simple build, all of the components can be bought cheaply on ebay or electronicsshops like futurlec.

<iframe src="//player.vimeo.com/video/26730692" width="560" height="371" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen> </iframe> <p><a href="http://vimeo.com/26730692">SousVide-o-Mator</a> from <a href="http://vimeo.com/eikeland">Stian Eikeland</a> on <a href="https://vimeo.com">Vimeo</a>.</p>

**Bill of materials:**

  * Atmega 328p (futurlec)
  * 3 Buttons (ebay)
  * 20x4 LCD (ebay)
  * Solid state relay (ebay - Futek) - you might need a heatsink, i use the metal plate in my enclosure.
  * Contrast potmeter
  * LED
  * 16 mhz crystal / ceramic resonnator
  * Basic resistors, capacitors, transistors
  * Enclosure, preferably with metal backplate (ebay..)
  * 220 IEC input and output sockets (futurlec..)
  * DS18B20 temperature probe (ebay..)
  * Stripboard (ebay)

In total I think it cost me around $30 to build it (not including the pump and rice cooker), not too bad. You could easily make it cheaper by using simpler items - do you really need a 20x4 LCD?

The source code is available at Bitbucket, please feel free to use it for whatever you want: [https://bitbucket.org/seikeland/sousvide/overview](https://bitbucket.org/seikeland/sousvide/overview)

[![sousvide schema](/images/2011-08-17-sousvide-o-mator/sousvide-schema.png)](https://bitbucket.org/seikeland/sousvide/src/9824aa7a5dda/schematics.png#)
Note that the schematic doesn't include power curcuit and programming headers.

I've built the controller into a simple case (w/ metal backplate) I found on ebay. If you have a powerful heater you should concider adding a heatsink to the SSR, they can get hot when switching large currents. With the metal-backplating, my 350W rice cooker never even make the SSR go above room temperature. See pictures of the case and setup below:

<figure>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7172.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7172.jpg" alt=""></a>
</figure>

<figure class='half'>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7183.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7183.jpg" alt=""></a>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7181.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7181.jpg" alt=""></a>
</figure>

<figure class='half'>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7185.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7185.jpg" alt=""></a>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7131.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7131.jpg" alt=""></a>
</figure>

<figure class='half'>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7127.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7127.jpg" alt=""></a>
  <a href="/images/2011-08-17-sousvide-o-mator/k7im7186.jpg"><img src="/images/2011-08-17-sousvide-o-mator/k7im7186.jpg" alt=""></a>
</figure>

Future work:

  * It needs a bit of calibration (the gain, integral and derivative) to avoid initial overshoot and small ocillations when changing load.
  * Add bluetooth, I have a small bluetooth to ttl module I can connect to get wireless serial (to interface it with pid controller gui)
  * Add internal power supply, should be enough space in the box for a 5v transformer.

Stay tuned for more posts on how to calibrate the beast :)

Also, check out [eGullet](http://forums.egullet.org/index.php?/topic/136274-sous-vide-index/) for more information about cooking Sous Vide (temperature tables.. recipes.. safety concerns..)
