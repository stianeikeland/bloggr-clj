{ :slug "go-gpio-library-for-raspberry-pi"
  :title "Go GPIO library for Raspberry Pi"
  :date "2013-07-30 18:23:28+00:00"
  :tags #{:go :golang :raspberrypi :programming}}

------

<!-- ![Gopher](http://stianeikeland.files.wordpress.com/2013/07/gopher.png?w=300) -->

![Gopher](/images/2013-07-30-go-gpio-library-for-raspberry-pi/gopher.png)

Been playing a bit with [Go-lang](http://golang.org/) lately, it seems like a fun little language. Very minimalistic and clean, yet quite powerful - you can do so many things with the simple constructs they provide (especially the goroutines, channels and type-system). Sadly, it looks like it's pretty slow in benchmarks compared to the other natural alternatives - but it's still early.

<!-- [![animated](http://stianeikeland.files.wordpress.com/2013/07/animated.gif)](http://stianeikeland.files.wordpress.com/2013/07/animated.gif) -->

<figure style="float: right; margin-left: 15px;">
	<img src="/images/2013-07-30-go-gpio-library-for-raspberry-pi/animated.gif">
</figure>

Anyway, in an attempt to mix Gophers and Pi, I've [made a small native GPIO library](https://github.com/stianeikeland/go-rpio) for Go on the Raspberry Pi (or the bcm2835 chipset in general). Nothing advanced, but it provides the usual suspects: PinMode (Output, Input), Write, Read, PullUp, PullDown, PullOff, etc. It works by memory-mapping the GPIO addresses in /dev/mem, so it will require root.

To use, and blink a little LED ("hello world" of the electronics/microcontroller realm) just do something like:

<!-- <div class="clearfix"></div> -->

~~~ go
import (
	"fmt"
	"github.com/stianeikeland/go-rpio"
	"os"
	"time"
)

func main() {
	if err := rpio.Open(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
	defer rpio.Close()

	pin = rpio.Pin(10)
	pin.Output()

	for x := 0; x < 20; x++ {
		pin.Toggle()
		time.Sleep(time.Second)
	}
}
~~~

Available over at [GitHub](https://github.com/stianeikeland/go-rpio). Would be awesome to add PWM, I2C, SPI, etc.. who knows, maybe one day..
