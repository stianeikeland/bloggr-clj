{ :slug "building-a-home-automation-system-part-1-intro"
  :title "Building a home automation system - Part 1 (intro)"
  :date "2012-07-08 21:17:09+00:00"
  :tags #{:homeautomation :jeenode}}

------

Ever since i bought my own apartment 1.5 years ago I've been thinking about adding a bit of home automation. Given an API to sensors, lighting, heating and other electronic devices there are so many smart things you could do.

Using temperature sensors and control over heating you could PID-control (see my sous-vide cooker) indoor temperatures way better than any regular termostat could. And it would be easy to add day and night temperatures. You could make it fully automatic (integrated with your calendar), or control it yourself (turn up heating remotely a cold winter day when you're on your way home).

Given access to lighting sensors (I already have a few LDRs running, logging lighting level to cosm) and lighting controls (dimmer) you could do lots of fun stuff. For example gradually increase lighting in the bedroom minutes before the alarm goes off in the morning. You could gradually increase lighting as the sun sets in the evening. You could automatically dim the lights when starting a movie in XBMC and revert to previous level on pause/stop. Given motion detectors (passive IR) you could turn off lights in empty rooms, or even have a "last-man-out" button next to your door that turns everything off.

I want automatic blinds (sun really strong and I'm not home? turn down the blinds..).. etc.. etc.. I want the coffee-maker to automatically disconnect 45 minutes after making a brew - and I want to start it from bed. There are so many possibilites.

<figure>
	<img src="/images/2012-07-08-building-homeauto/dsc_2490_large.jpg" alt="">
</figure>

Some time back I bought a small collection of jeenodes, they are small boards with atmega328s (arduino compatible) and a HopeRF 868 mhz radio (RFM12B). The design is open source, so you can make them yourself or buy them from jeelabs: [http://jeelabs.com/products/jeenode](http://jeelabs.com/products/jeenode)

<figure>
	<img src="/images/2012-07-08-building-homeauto/screen_shot_2012-07-08_at_23-01-09.png" alt="">
</figure>

I'm using a couple of them with DS18B20 temperature sensors and LDR (lighting sensor) to collect and graph temperature/lighting data via cosm.com. They have performed really well, and the 868 mhz spectrum seems mostly clean here were I live. I made a quick spectrum analyzer last year to have a look:

<iframe src="//player.vimeo.com/video/19636898" width="560" height="361" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen> </iframe> <p><a href="http://vimeo.com/19636898">RFM12b+Jeenode based 868 Mhz spectrum analyzer</a> from <a href="http://vimeo.com/eikeland">Stian Eikeland</a> on <a href="https://vimeo.com">Vimeo</a>.</p>

For a home automation system there is lots of choices to be made, both for hardware and for software. I'll try to make a series of this describing the solution I go for. One of the first steps will be to find a suitable (wireless) hardware power control solution (on/off + dimming for lights and power sockets). I want it all to be event-driven, and need some way of passing events from component to component - a message bus of some sort. In the next posts in this series I'll look at that.

