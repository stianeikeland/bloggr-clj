---
author: stianeikeland
comments: true
date: 2012-10-07 13:10:33+00:00
layout: post
slug: flocking-synchronization
title: Flocking / Synchronization
image:
  feature: 2012-10-07-flocking-synchronization/feature.jpg
wordpress_id: 130
tags:
- boids
- flock
- flocking
- programming
- swarm
---

There are a few behaviors in nature that seems really advanced, impressive and organic, that actually can be simulated on a computer using only a few simple rules. One of them is flocking - the "formation"-flying of birds or movement of schools of fish. There's an old algorithm/simulation by Craig Reynolds called Boids (often used in screensavers and even used for modeling the flying bats in batman) that explains this pretty well. Make a few objects that react to their neighborhood according to the following three simple rules:

  * Separation - try to stay at least x units away from all neighbours.
  * Alignment - try to match the average direction of your neighbours.
  * Cohesion - steer towards the center location of your neighbours.

<iframe width="560" height="315" src="http://goo.gl/eY8KY" frameborder="0"> </iframe>

Implement the rules and depending on how you weight them you quickly get behavior that looks impressively organic. Was bored the other evening, and my coding fingers are itching since I've barely gotten to do any programming at work these last months - the meeting/planning vs programming ratio is through the roof. So I did a quick implementation for shits and giggles. Check it out here: [http://goo.gl/eY8KY](http://goo.gl/eY8KY) (or click the image above). I have no idea if it works with all browsers, but works with chrome at least.

There's also a decent TED talk on the subject:

<figure>
	<a href="http://www.ted.com/talks/steven_strogatz_on_sync">
		<img src="/images/2012-10-07-flocking-synchronization/ted.jpg">
	</a>
</figure>

What's it going to be used for? Notting really, but might be useful to know if I ever get to chance to program a large army of hunter killer robots with swarm capability.
