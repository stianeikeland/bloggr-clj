{ :slug "dead-cheap-li-ion-charger-tp4056"
  :title "Dead cheap li-ion charger - TP4056"
  :date "2013-01-23 21:12:25+00:00"
  :tags #{:electronics :18650}}

------

[![Image](/images/2013-01-23-dead-cheap-li-ion-charger-tp4056/k7im9556.jpg)](/images/2013-01-23-dead-cheap-li-ion-charger-tp4056/k7im9556.jpg)

There's a chip called TP4056 that's quite good for charging li-ion/li-po cells. You can buy the chip really cheap, or like above - a finished board with usb power in for $1.50 (!) on [ebay](http://www.ebay.com/sch/i.html?_trksid=p3984.m570.l1313&_nkw=lithium+battery+charger+board&_sacat=0&_from=R40) including shipping. I bought one, soldered on a couple of silicon wires with magnets attached to the end. Also added a plug-able tiny voltmeter ([ebay](http://www.ebay.com/sch/i.html?_trksid=p2047675.m570.l1313&_nkw=lipo+battery+tester&_sacat=0&_from=R40), but this other one from [fasttech](http://www.fasttech.com/product/1222901) also looks quite good). I'm not sure how the voltmeter affects the charging (as it feeds off the charging current), but I recon it should be quite fine.

<figure style="float: right; margin-left: 15px;">
	<img src="/images/2013-01-23-dead-cheap-li-ion-charger-tp4056/resistor-1.png">
</figure>


Per default it uses 1 amp of charging current, which is quite good for large cells, but you can modify it by changing one of the onboard resistors - lower values and you can use it for smaller cells safely.

It's quite a decent little charger, it manages pretty close to true CC/CV (constant current / constant voltage). It does get a bit hot when charging at 1 amp, especially in the start when the voltage difference between input and output is large, but nothing too worrying.

Termination seems to happen according to spec - just around 4.20 volt.

All in all - quite happy with it. I recon the chip (or even the board) could easily be built into projects that need li-ion/po charging, or even into a battery pack. Can't beat the price! :)
