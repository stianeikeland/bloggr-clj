{ :slug "garage-opener"
:title "Bluetooth LE garage opener"
:date "2017-03-27 14:00:00+0200"
:image "/images/2017-garage/banner.jpg"
:video "https://www.youtube.com/watch?v=1c1G_Ok_XsM"
:tags #{:electronics}}

------

The yak I'm trying to shave here is the garage opener (keychain-)remote. You
know those big clunky remotes people carry around on their keychains? I try to
maintain a minimum (viable?) keychain and I absolutely hate keychain remotes
and big keys with a passion.

Remotes are fine in the car, but I don't want to carry one. The garage in
question here only have access through the main port - no door. And since I live
on a hill, with a garage down by the road, connected by long flight of stairs,
it's annoying to have to go all the way to fetch the remote when you just want
to grab something real quick from the garage..

<figure> <iframe width="560" height="315"
src="https://www.youtube.com/embed/1c1G_Ok_XsM?VQ=HD720" frameborder="0"
allowfullscreen></iframe> <figcaption>My Bluetooth LE garage
opener.</figcaption> </figure>

I usually carry my iPhone, so my thinking was that it could work as a decent
remote.

# Requirements:

- Connect to iPhone wirelessly somehow..
- No wifi from house available (garage is in a wifi-shadow)
- Connect to existing garage door opener expansion port.
- Powered directly from garage door opener bus (24v max 50ma)

# Connectivity

Wifi is out of the question since it's hard to reach without running cables and
putting up antennas on both locations (and no, I don't want to connect to a
garage-AP from my phone).

A cellular connected microcontroller would probably work, but I wanted to avoid
that. And then, there's Bluetooth. And more importantly - Bluetooth Low Energy
(BLE).

# Microcontroller

I looked around for a microcontroller with integrated bluetooth (and preferably
an available cheap dev board with an external antenna). The nRF51822 sparked my
interest. It's cheap, packs a 32 bit ARM Cortex M0 core and supports BLE.

I found a small PCB with all the required supporting electronics from CJMCU
(probably designed for use in a cheap drone? as it includes a LIS3DH
accelerometer). By adding a 24v -> 3.3v step down voltage regulator, a switch, a
led, an optocoupler (for triggering the garage door), and a few inputs for
magnetic reed switches (for detecting door open or closed) I pretty much had a
working prototype.

<figure class='half'>
    <img src="/images/2017-garage/nrf51822.jpg">
    <img src="/images/2017-garage/app.jpg">
    <figcaption>Microcontroller board and the iOS app.</figcaption>
</figure>

# App

I've never really done any serious iOS development. But I launched Xcode and
managed to hobble together some lines of swift code that connects to a bonded
BLE device, exchanges some secrets, and makes a "Sesame!"-button go solid blue
when the garage is within range. It's not pretty, but it works.

Now - the bad parts. Unless you pay Apple, then your app will stop working after
7 days (free signing cert only lasts for a week). Paying for proper membership
costs $99. Refreshing it every 7 days is too much of a chore, so I ended
shelling out for a proper developer account. (This was the most expensive part
of the whole project)

I also looked into CarPlay, would be pretty cool if you could open the garage
from a car's interface, but sadly it seems that Apple have locked this down and
requires you to register for some kind of special entitlement license. :(

# Firmware

I might release the source code for the firmware, but there's a few things I
want to iron out first. Overall this was probably the biggest learning
experience, I've done some bluetooth stuff before, but BLE is a completely
different beast (with it's own nuances, own lingo, etc..)

Making it secure was also harder than it should be, IMHO, there was way too much
fidling around to make a decently secure BLE device.

I implemented security the following way:

1. I've hooked up a physical button on the device to put it in pairing mode. You
   have to manually implement "pairing"-mode and "secure"-mode by selectively
   broadcast your services, and once in secure mode maintain a whitelist of
   authorized devices.
2. After pairing you can exchange long term secure keys, this is called bonding.
   This makes future communication encrypted.
3. And, in an effort of cooking bacon in butter, I exchange an application layer
   secret while opening the door (just as an extra layer of security in case i
   fucked something up in the BLE communication layer).

I'm not going to go much into details about how to do this, but I recommend
looking at this
[BBC micro:bit firmware](https://github.com/lancaster-university/microbit-dal)
(also based on nRF51822) for how to set up a whitelist, pairing/bonding,
broadcast services, etc.

And to be honest, what's easier if you want to break into a garage? A 2 minute
job with a crowbar or hours of sig int and analysis?

# Conclusion

The opener has been running for a few months now, and it's been working great
for the most part. Sometimes the app is a bit slow to connect, or even require
me to get quite close before it manages to detect the device. I've a few ideas
how to iron this out in software (or the nuclear option of hooking it up to an
outside 2.4 ghz panel antenna) - but it hasn't annoyed me enough yet to make me
put in the effort :)

TLDR; Just watch the video...
