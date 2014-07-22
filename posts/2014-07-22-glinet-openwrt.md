{ :slug "gl-inet-openwrt"
:title "GL.iNet Openwrt router"
:date "2014-07-22 08:00:00+01:00"
:image "/images/glinet/header.jpg"
:tags #{:openwrt}}

------

I recently bought an interesting little router on eBay, a
[GL.iNet](http://www.ebay.com/itm/Portable-Smart-Router-GL-iNet-3G-OpenWrt-Mobile-App-control-16M-flash-/221462303933?pt=COMP_EN_Routers&hash=item33902e94bd)
router. Pretty cheap, about $25 + shipping.

It sports the same form factor as the sweet little
[TL-WR703n](http://wiki.openwrt.org/toh/tp-link/tl-wr703n) - everyones favorite
hackable cheap linux router. Not only is the form factor the same, it's the same
SOC - a AR9330.

Here the similarites end however. Many people (me included) modify their WR703n
routers, upgrading the hardware with larger flash, more ram, break out GPIO,
etc, etc. And guess what - the GL.iNet router has all of these modifications -
out of the box!

<figure>
<a href="/images/glinet/board.png"><img src="/images/glinet/board.png"></a>
</figure>

* AR9330 SOC (same).
* 64 Mb ram (vs 32 Mb).
* 16 Mb nand flash (vs 4 Mb).
* Serial UART on 3 header pin (vs serial on pads).
* Friendly row of gpio broken out (vs pads spread all over the board).
* 2 ethernet (vs 1 ethernet).
* USB2 (same)

It's like a pre-modded WR703n on steroids! Awesome!

The backside of the device has a sticker with all the settings you need, IP,
SSID, passwords, etc, they even set up a ddns address for your device, making it
availble from their domain (many people will probably want to disable this..)
which I guess can be of great convenience to some.

If you enter the WebUI, it actually looks pretty nice, it's preloaded with stuff
for downloading (web, torrent, etc) and sharing an external drive, or streaming
from a web camera, etc. You're met with a decent wizard that helps you set up
the device.

<figure class="half">
<a href="/images/glinet/glinet2.png"><img src="/images/glinet/glinet2.png"></a>
<a href="/images/glinet/glinet1.png"><img src="/images/glinet/glinet1.png"></a>
</figure>

But.. guess what happens if you press the advanced settings button - boom! The
OpenWRT webui.. It's actually running Barrier Breaker from OpenWRT Trunk, and a
pretty recent version at that. They just add their own stuff on top of the
immensly powerful OpenWRT distribution. All the goodies are right under the hood - and you can easily do a clean OpenWRT-install if you do not want any of the GL-iNet stuff.

This is the device to get if you need a cheap embedded low-powered linux device,
with usb2, gpio, lan, wifi, etc for a project.

They even offer [instructions](http://www.gl-inet.com/w/?p=398&lang=en) on how to build a custom image for their router - how cool is that?

Though, I do wonder if the device is up to the best practices for the SOC,
because the component count feel quite low vs the WR703N. One of the big
components missing is the H1601CG ethernet transformer. Maybe they do the job
using discrete passives, but the passive count is pretty low as well. This
is a field where my knowhow is sorely lacking, so I really have no idea - take
this with a metric grain of salt - I've had no issues with the device thus far.
