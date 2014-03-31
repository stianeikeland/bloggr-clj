{ :slug "building-a-battery-pack-for-35w-hid-canister-light-part-1"
  :title "Building a battery pack for 35W HID canister light - part 1"
  :date "2011-11-17 15:19:36+00:00"
  :tags #{:electronics}}

------

I had a bit of luck recently and got a 35W HID canister light from a friend for a good price. His was broken and he had already bought a new one, so I had a go at it. The lamp and driver curcuit seemed to be working, but the battery pack was dead. Anyway, building a battery pack should be a doable build, so I bought it from him.

Canister lights are high powered lights often used by divers, a very popular choice is to use 21W HID bulbs (HID - High Intensity Discharge - the same type of bulbs used in modern cars as head lamps). The one I got is a quite powerful 35W version (some people even use 50W versions - but mostly for video lights).

<figure class='third'>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/salvo20202021.jpeg" alt="">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/hlp8000c2.jpeg">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/4345.jpeg">
</figure>

The original battery pack was basically 6 rectangular Li-Po 10 Ah cells, connected in 2 series with 3 cells each (and balance charger circuits + undervoltage/overvoltage protection). They were similar to these from [batteryspace.com](http://www.batteryspace.com/highpowerpolymerli-ioncell37v10ah9759156-5c37wh50arateunapproved.aspx) (which also sells exactly the same protection [circuit](http://www.batteryspace.com/pcmwithequilibriumfunctionandfuelgaugefor111vli-ionbatterypackat10alimit.aspx)). The original pack gave the light about 5.5 hours runtime, I thought about making a similar pack - but I rarely need that much runtime. Most of the dives I do are only 45-60 minutes long. Instead of going the dangerous Li-Po road (check out youtube, these things explode and burn if handled incorrectly)

[youtube http://www.youtube.com/watch?v=zQheOtdCTjs]

.. and the batteries in the videos are usually much smaller than the large 20 Ah pack the canister originally had. I decided to go with the much safer NIMH (nickel-metal hydride) cell route. NIMH are much less energy dense per unit of weight, but much easier to handle and doesn't need elaborate protection circuits. Having a heavy canister is actually good for diving, it just replaces weights I would have to bring in my belt anyway. I looked around for different cells that could work for my canister, 4/3AF (1.2v 4500 mah) seemed interesting, but in the end I settled on Sub-C 1.2v 5000 mah cells from Tenergy (Propel).

<figure class='half'>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/15738.jpeg" alt="">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/top-12v-tenergy.jpg">
</figure>

My goal was to build a pack with about 10 Ah of umph at 12 volt.. So 2 series (10 cells each) in parallel would give me 10 Ah 12 volt. Having large parallel series can however be a problem.. if one cell in a series short circuit internally, then the other series would dump all it's power to it (massive overload). To avoid this I plan to isolate the series using a beefy [schottky diode](http://en.wikipedia.org/wiki/Schottky_diode), and also charge them seperately.

<figure class='third'>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/celler.jpg" alt="">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/img_0746.jpg">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/img_0747.jpg">
</figure>

<figure class='half'>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/img_0749.jpg">
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/img_0750.jpg">
</figure>

I built the pack using 6 rows of 3 batteries (soldered together), and the final two batteries added to the middle. Cut out some plastic lid for top and bottom, and then heat shrink wrapped it all. At the moment I've used some old JST connectors, but I'm waiting to get XT-60 connectors as the JSTs are running a bit too warm for comfort when the light is on.

<figure>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/acc62.jpg" alt="">
</figure>

For charging I bought two pretty cool microcontroller based chargers from Hobby King: [Turnigy Accucel-6](http://www.hobbyking.com/hobbyking/store/uh_viewItem.asp?idProduct=7028). They charge pretty much everything you throw at them - and they only cost $23 each!


<figure>
	<img src="/images/2011-11-17-building-a-battery-pack-for-35w-hid-canister-light-part-1/img_0765.jpg" alt="">
</figure>

I've just finished some runtime tests (HID lamp submerged in the kitchen sink), the series have been running for about 1h20m each now. I have not received the diodes yet, so no parallel connection. Anyway, I should have at least 2 hours and 40 minutes of runtime - probably even more (you shouldn't discharge the cells below 0.9v, but there are still around 1.1v left). I'm guessing I should be able to get 3 dives out of this thing when it's fully charged.

Next step is to add XT-60 connectors, diodes, and try it out on a dive :)
