---
author: stianeikeland
comments: true
date: 2012-09-15 13:08:59+00:00
layout: post
slug: better-wr703n-antenna-mod
title: Better WR703N Antenna mod
wordpress_id: 93
---

I blogged about a WR703N antenna mod [earlier](/wr-703n-external-antenna-mod-diy), I found some hints about a better way of doing it on the [openwrt forum](https://forum.openwrt.org/viewtopic.php?pid=173729#p173729). There's a (0 ohm) shunt resistor in the antenna path - labeled J1. I undid the changes I did earlier, unsoldered the J1 resistor.

With the board oriented with the wired lan to the left, I then soldered the core of the antenna wire to the RIGHT pad of the now removed J1 resistor. There is a big empty pad "north" of J1, where the antenna shield can be soldered.

<figure>
	<img src="/images/2012-09-15-better-wr703n/k7im9483-note.jpg" alt="">
	<img src="/images/2012-09-15-better-wr703n/k7im9484.jpg" alt="">
</figure>

EDIT: See [Diarmaid Ó Cualain](http://twitter.com/DiarDan) comment below, he has been getting better luck leaving J1 bridged (and instead cutting the internal antenna) - while still soldering to the same location. There are two capacitor on the antenna track on the other side of J1, which could be the reason for this.
