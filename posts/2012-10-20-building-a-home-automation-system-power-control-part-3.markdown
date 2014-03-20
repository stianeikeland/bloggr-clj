---
author: stianeikeland
comments: true
date: 2012-10-20 14:49:47+00:00
layout: post
slug: building-a-home-automation-system-power-control-part-3
title: Building a home automation system – Power control (Part 3)
wordpress_id: 138
categories:
- homeautomation
tags:
- coffeescript
- home automation hardware
- homeautomation
- nodejs
- rfxcom
- rfxtrx
- rfxtrx433
---

Just got myself a RFXtrx433 USB transceiver from [RFXcom](http://www.rfxcom.com/transceivers.htm). It's a tiny box which allows you the communicate with a wide range of home automation hardware that uses the 433 MHz band, check out the impressive list of supported hardware on their website. There is one store in Norway that sell these, and guess what, it's in Strandgaten here in Bergen (walking distance) - [http://www.smarthus.info/](http://www.smarthus.info/)


<figure class="third">
  <a href="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/rfxtrx.jpg">
    <img src="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/rfxtrx.jpg">
  </a>
  <a href="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/nexa3.png">
    <img src="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/nexa3.png">
  </a>
  <a href="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/nexa-button.png">
    <img src="/images/2012-10-20-building-a-home-automation-system-power-control-part-3/nexa-button.png">
  </a>
</figure>

There's a guy named [Kevin](http://bigkevmcd.com/) that have been working a nodejs library for it, available here: [node-rfxcom](https://github.com/bigkevmcd/node-rfxcom). He's made support for events and triggering of most of the protocols - it's is under active development at the moment. It works quite well here for lighting2 equipment (Nexa / Home Easy based). I added support for lighting1 events (ARC - older protocol - uses code wheel), since it seems that Nexa still sell some equipment using this (the single wall button in my case).

So far I'm only using cheap Nexa plug-in modules and wall buttons, which seem to work well enough, but the plug-in modules aren't exactly pretty. A lot of the lighting in my apartment is also hard-wired (halogen spots in the roof, no sockets), so sooner or later I'm going to have to get some real built in modules. This has to be done by a certified electrician and I want to be sure that I go for the right technology. I got a tip that insteon is planning on releasing european modules soon(tm), these are mesh-networked (bonus!). And since every receiver is also a sender/repeater I'm hoping they can be queried: "Hello lighting module A, what is your current dimming level?"

I have the RFX unit up and running as a service on my home automation message bus now, so now it's finally possible to subscribe for events from transmitters (ex: wall switch) and send orders to receivers (ex: plug in modules connected to lights). Subscribing to the coffee-machine and turning it off after 60 minutes, or turning off several lights/appliances if the last-man-out button near the exit door is pressed can now be done by:

~~~ coffeescript
power = new Power new MessageBus

# Power coffeemaker off 60 minutes after it was powered on:
power.on 'kitchen-coffeemaker', (event) ->
	turnOff = () ->
		power.send {
			command: "off",
			location: "kitchen-coffeemaker" }
	setTimeout turnOff, 60*60*1000 if event.command is "on"

# Turn stuff off if the last-man-out button near the exit is pressed:
power.on 'exitdoor-lastmanout', (event) ->
	receivers = [
		"kitchen-coffeemaker",
		"kitchen-kettle",
		"bedroom-lights",
		"bathroom-lights"]
	power.send {
		command: "off"
		location: receiver
	} for receiver in receivers if event.command is "off"
~~~

Now I need to find some suitable PIR (passive infra-red) motion sensors to use with the system, and also try to research which in wall technology to go for in the future - so that I can control more of the lighting here at home.
