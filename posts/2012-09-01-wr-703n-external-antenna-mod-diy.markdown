{ :slug "wr-703n-external-antenna-mod-diy"
  :title "WR-703N External Antenna Mod (DIY)"
  :date "2012-09-01 11:54:06+00:00"
  :tags #{:electronics}}

------

**UPDATE: Don't do this mod, there are better techniques. Example: Solder directly to PCB antenna, or trying the J4 bridge mod as described here: [better-wr703n-antenna-mod](/2012/09/15/better-wr703n-antenna-mod/)**

Maybe you've heard about the TP-Link WR-703N router? It's a small "pocket"-router with wlan, 1x lan, 1x usb, Atheros chipset, 32 mb ram, 4 mb flash. It can run OpenWRT, and it's really really cheap ($23 on ebay currently). They require very little power to run (100ma ish) and are powered via a micro-usb connector. And they have great hack potential (come on.. $23 for a linux thingy with lan, wlan, usb, gpio, etc..). You can upgrade the flash memory to at least 8 Mb, and even upgrade RAM to 64 mb if you have access to a hot air rework setup.

I've received two, and have just ordered a third, and have quite a few projects I want to use them for. Battery powered mesh network is one, great for getting networking in weird locations, they can run for many hours on a single 18650 battery. Also thinking about using one for a "umbrella warning sign" near the front door (I live in Bergen (Norway), a city with over twice the annual precipitation of the "Rain City" Seattle). A simple laser cut acrylic umbrella logo with the router's pcb behind - using GPIO to drive a LED if rain is expected the next 8-10 hours.

[Ebay link](http://www.ebay.com/sch/i.html?_trksid=p5197.m570.l1313&_nkw=wr703n&_sacat=0) (be sure to buy the blue version!)

<figure>
	<img src="http://s3.tadkom.net/wr703n/K7IM9473.jpg" alt="">
</figure>

The routers have a tiny internal PCB-antenna, so on one of them I've added an external SMA connector. There are several ways of doing this, one is to solder the new antenna connector to the top of the existing PCB-antenna. A chinese website (google translate..) suggested desoldering a capacitor "upstream" and connect to the pads it used. I tried this, but will try both to see which works the best later.

<figure>
	<img src="http://s3.tadkom.net/wr703n/pcb-1.jpg" alt="">
</figure>

Buy a SMA connector on Ebay with pigtail. Pry the blue cover off the router. Extract the PCB. Locate C114 on the backside. Desolder it (easiest is to place the soldering iron parallel to the capacitor so that you're heating both pads). Solder the antenna core to the pad closest to the flash memory IC. Solder the antenna shield to the other pad (see image above). Cut the existing PCB antenna track.

<figure>
	<img src="http://s3.tadkom.net/wr703n/K7IM9470.jpg" alt="">
</figure>

Use a 6mm drill bit to make a suitable hole for the antenna connector (make sure there is room for the PCB under the connector).

<figure>
	<img src="http://s3.tadkom.net/wr703n/K7IM9478.jpg" alt="">
</figure>

