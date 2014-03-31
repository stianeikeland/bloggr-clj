{ :slug "building-a-home-automation-system-the-broker-and-sensors-part-2"
  :title "Building a home automation system - The broker and sensors (Part 2)"
  :date "2012-09-24 16:31:33+00:00"
  :tags #{:homeautomation :jeenode :electronics}}

------

Every now and then (when I have time..) I do a [bit of work](https://github.com/stianeikeland/homeautomation) on what's going to become my home automation system. At the moment it's pretty basic, it's simply events going over a message broker that small stubs of code react to. Most of it runs on my [raspberry pi](http://en.wikipedia.org/wiki/Raspberry_pi) and a few jeenodes (atmega328 + hoperf rfm12b radio) as wireless sensors.

Everything connects to a simple message broker built on [ZeroMQ](http://www.zeromq.org/) - and I love it! ZeroMQ is so great to work with, it takes most of the hassle of network socket programming away leaving you to concentrate on what really matters. There is no daemons to deal with (as with most other message buses), it's lightning fast, but doesn't really handle persistant messaging.

At the core of the system I have this running on my raspberry pi:

~~~ coffee
# Router / Central hub for home automation.
# Receives events and distributes via pub/sub

zmq = require 'zmq'

inputPort = 'tcp://*:8888'
outputPort = 'tcp://*:9999'

# Pull socket for incoming (push/pull), pub for outgoing (pub/sub)
input = zmq.socket 'pull'
output = zmq.socket 'pub'

input.identity = 'brokerin' + process.pid;
output.identity = 'brokerout' + process.pid;

output.bind outputPort, (err) ->
    throw err if err
    console.log "Publisher listening on #{outputPort}"

input.bind inputPort, (err) ->
    throw err if err
    console.log "Pull listening on #{inputPort}"

input.on 'message', (data) ->
    try
        jdata = JSON.parse data
        if not jdata.event?
            jdata.event = "unknown"

        strData = JSON.stringify jdata
        console.log "Relaying packet of type: #{jdata.event} >> #{strData}"
        output.send [jdata.event, strData]

    catch error
        console.log "Invalid data: #{error}"
        console.log data.toString()

process.on 'SIGINT', () ->
    input.close()
    output.close()
~~~

It's very simple - just two ZeroMQ sockets - one running in a push/pull configuration, another running in a pub/sub configuration. A relay for my sensor nodes will connect to it and push it JSON formated data from the different sensors located in my apartment.

~~~ json
{
    "nodeid": 2,
    "temperature": 2.19,
    "voltage": 335,
    "counter": 1215,
    "event": "sensor",
    "timestamp": "2012-09-24T15:49:28.369Z",
    "location": "refrigerator"
},
{
    "nodeid": 3,
    "temperature": 23.5,
    "voltage": 369,
    "counter": 20,
    "event": "sensor",
    "timestamp": "2012-09-24T15:52:08.450Z",
    "location": "livingroom-bookshelf"
}
~~~

The broker will relay events such as these, the two examples are wireless sensor nodes located in my refrigerator (currently holding 2.19C and battery voltage of 3.35v) and the other one in my livingroom bookshelf (2.19C and 3.69v). The broker will receive these (and others), and send them out to subscribers of sensor events. It makes it really easy to make something that reacts to the temperature, you can connect to the broker and subscribe to sensor events in 2-3 lines of code in most languages. Example:

~~~ coffee
sub = (require 'zmq').socket 'sub'

sub.connect 'tcp://raspberrypi:9999'
sub.subscribe 'sensor'
sub.on 'message', (topic, data) -> console.log "#{topic} => #{data}"
~~~

As you can see, it would be very simple to expand this and create something that let me know if a battery of a sensor was running low. Or something that would PID adjust a heating source in the living room. Or something that would warn me if temperature in the fridge was running high.. or.. or.. or.. Beauty of it is that you don't have to think about network stuff like disconnects..

I've made a small program in C# running on my HTPC that registers a few global hotkeys. If I press ctrl-alt-o then a small event will be sent out from the C# program, to the message bus, to everyone that subscribes to "receiver" events. There is a small program that does this and relays the message to a microcontroller, which in turn talks to my Pioneer VSX-2016av surround receiver via the SR bus I talked about in an earlier post. For example, sending events like these to the broker will toggle the power of the surround receiver and turn the volume up 5 steps.

~~~ json
{
    "event": "receiver",
    "action": "power"
},
{
    "event": "receiver",
    "action": "volumeup",
    "count": 5
}
~~~

<iframe width="560" height="315" src="http://www.youtube.com/embed/Zu3gNf3N33s" frameborder="0"> </iframe>

Quick example above, sorry for the camera shake.

It's getting there, now I have sensor data and control over the surround receiver. The next steps will be to expand the available sensor data - I wish to add a DHT22 Humidity sensor and also a light sensor (LDR?) to the sensor nodes soon. And also maybe PIR (passive ir). I also need to add control over power soon (lighting and heating).

<figure>
    <img src="/images/2012-09-24-building-homeauto/jeenode.jpg">
</figure>

At the moment they are only using a DS18B20 temp sensor. When I have all the components in place I'll make a small PCB with room for all the components, the radio and a smaller atmel microcontroller. Currently they take up a bit more space than needed.
