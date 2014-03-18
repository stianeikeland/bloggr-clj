---
author: stianeikeland
comments: true
date: 2011-03-20 14:40:00+00:00
layout: post
slug: msp430-coffeetimer
title: MSP430 Coffeetimer
wordpress_id: 11
tags:
- coffee
- electronics
- microcontroller
- msp430
- programming
---

The [MSP430](http://en.wikipedia.org/wiki/TI_MSP430) is a familty of cheap microcontrollers from Texas Instruments featuring ultra low power usage. TI offers a devkit with everything you need to get started (USB-based development board and 2 microcontrollers from the value line) called the [MSP430 Launchpad](http://processors.wiki.ti.com/index.php/MSP430_LaunchPad_(MSP-EXP430G2)?DCMP=launchpad&HQS=Other+OT+launchpadwiki). The killer - it's dead cheap - $4.30 for two microcontrollers and the launchpad including shipping (fedex!). You can also probably manage to get a few MCUs for free as samples. TI is probably selling the kit at a loss really trying to get into the hobbyist market, which is primarily dominated by the [Arduino](http://arduino.cc/) (and to a lesser degree Atmel in general and Microchip Pic). I was very impressed by the power usage, a guy managed to run them for [10 weeks](http://kennethfinnegan.blogspot.com/2010/09/msp430-low-power-experiment.html) as a clock on a couple of capacitors (granted - 10F caps).


![Msp430_launchpad_1](http://stianeikeland.files.wordpress.com/2011/03/msp430_launchpad_1.jpg?w=300)
![Launchpad](http://stianeikeland.files.wordpress.com/2011/03/launchpad.jpeg?w=230)



I have a 1 hour safety timer connected to my coffee maker (one of those devices that tick down and cut the power after one hour). Being a really cheap device from a low cost "wallmart" like shop I guess it was no surprise when it broke. It doesn't tick down anymore, seems like the motor burned out - it's mostly mechanical with a disc rotating one revolution and then cut the power by physically hitting a switch. I liked having the extra safety this device provided - and wanted a new one. Figured it was a good chance to learn some MSP430 programming. Turning the coffee maker on and off using a relay would be pretty simple, but I really don't want to fuck with 220V mains and leave it without supervision (which probably also voids my home insurance).

I have a few radio controlled Nexa power switches (which are CE certified), so I hooked up one of the MSP430 microcontrollers from the launchpad kit to a remote with a few transistors. Programmed the microcontroller to sleep and wait for a interrupt from small push button, on interrupt it turns on the coffee maker (via the remote - connected with two transistors) and starts a timer. The timer blinks a led every now and then before turning off the coffee maker one hour+ later. Put the whole thing in a small ikea plastic container with 2 AA-batteries, which should last severals years if the MSP430 is as power efficient as they say.


[![Photo_mars_20_3_32_47_pm](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_32_47_pm.jpg?w=300)](http://getfile4.posterous.com/getfile/files.posterous.com/temp-2011-03-20/kIIrtlAooggCdeoedzeAtmbsivCHbCpocElneEzpdmiuDeqcibuhwzfBAIar/Photo_mars_20_3_32_47_PM.jpg.scaled1000.jpg)
[![Photo_mars_20_3_32_58_pm](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_32_58_pm.jpg?w=300)](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_32_58_pm.jpg?w=300)
[![Photo_mars_20_3_33_10_pm](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_33_10_pm.jpg?w=300)](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_33_10_pm.jpg?w=300)
[![Photo_mars_20_3_35_18_pm](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_35_18_pm.jpg?w=300)](http://stianeikeland.files.wordpress.com/2011/03/photo_mars_20_3_35_18_pm.jpg?w=300)


There's a few gotchas when programming the MSP430s, for example the watchdog timer.. The MCU features a watchdog that resets the device after a short time (VERY annoying..), unless you disable the timer - it's on by default. Other than that it was more or less straight forward. You can either use the supplied eclipse-based code composer from texas instruments (proprietary and windows only) - or the open source [msp430-gcc](http://mspgcc.sourceforge.net/) toolchain (runs on both linux and mac).. Code written in the code composer will probably not compile using msp430-gcc, there seems to be different syntax for defining interrupts - and intrinsic functions often have different names and functions depending on which compiler you use. I've tried both, the TI software nicely integrates a debugger that can step through lines and instructions in the IDE as they are running on the chip - Spy-Bi-Wire (pretty awesome feature for a $4.30 pricetag). For the open source toolchain you can use mspdebug to open a network port where you can connect using gdb.. providing you with more or less the same features, but it's way more cumbersome to use gdb than a IDE-integrated debugger.. But then again, I don't often use a debugger (even for normal software - my method of debugging often involves throwing prints everywhere), so I guess having to not run the proprietary IDE in a virtual machine is worth the loss of the IDE integrated debugger.

Also, setting up the internal timers required reading quite a lot of TI documentation, more so than other microcontrollers I've tried. The community using it is also small and there are few libraries available. But anyway, got it running - and now it turns on and off my coffee maker.

In conclusion, I don't think TI will make that big of an impact on the hobbyist market with this, the alternatives are easier to use, have better documentation, usefull libraries and less cumbersome development tools. But then again - it's a really cheap way to get started on MCUs.

Posting the coffee timer code as a future reference, maybe some of the boilerplate code can be useful for others as well.

{% highlight c %}
/*
* Coffetimer (safetytimer)
*
* Activated when button (pin 4) is grounded.
* Connected to a RF - remote control on pin 5 and 6
* via a couple of transistors, controling the
* coffemakers power outlet.
*
* */

#include  "msp430g2231.h"

#define LONGDELAY (1000000 / (8*8)) * 4
#define SHORTDELAY (1000000 / (8*8*8))
#define POWERONTIME 4500

const int redLed = BIT0;
const int button = BIT4;
const int powerOn = BIT5;
const int powerOff = BIT6;

volatile int powerActive = 0;
volatile unsigned int onTime = 0;
volatile int blinkState = 0;

unsigned int i, y;

void main(void) {

    WDTCTL = WDTPW + WDTHOLD;

    P1DIR = ~button; // Button is input, rest output..
    P1OUT &= ~(powerOn | powerOff | redLed);

    P1REN |= button; // Pull up
    P1IES |= button; // High to low interrupt
    P1IE  |= button; // Interrupt on button..
    P1IFG = 0;       // Clear INT flag

    // Use internal occilator as timer source.. / 8.. /8
    BCSCTL1 = CALBC1_1MHZ; // Set range
    DCOCTL = CALDCO_1MHZ;   // SMCLK = DCO = 1MHz
    BCSCTL2 |= DIVS_3; // Divder / 8 for smclk
    TACTL = TASSEL_2 + MC_1 + ID_3 + TACLR; // Timer A, SMCLK src, up mode, divider / 8..

    CCTL0 = CCIE; // capture/compare interrupt go!
    CCR0 = SHORTDELAY;

    __enable_interrupt();

    while(1) {
        powerActive = 0;
        onTime = 0;

        // Go to sleep, wait for button
        P1IFG = 0;
        LPM4;

        // Waking up from button push..
        // Blink and power on coffemaker..
        for (i = 0; i < 4; i++) {
            P1OUT ^= redLed | powerOn;
            for (y = 0; y < 40; y++)
                __delay_cycles(40000);
        }
        P1OUT &= ~(redLed | powerOn);

        powerActive = 2;

        LPM1;

        // Waking up from timer.. blink and turn off coffeemaker..
        for (i = 0; i < 12; i++) {
            P1OUT ^= redLed | powerOff;
            for (y = 0; y < 40; y++)
                __delay_cycles(40000);
        }
        P1OUT &= ~(redLed | powerOff);
    }
}

// Button interrupt
#pragma vector=PORT1_VECTOR
__interrupt void port1_isr(void)
{
    if (powerActive == 0) {

        // Power on coffeemaker..
        powerActive = 1;
        LPM4_EXIT;

    } else if (powerActive == 2) {

        // Power off coffeemaker ahead of time..
        onTime = POWERONTIME + 1;
        P1OUT &= ~redLed;
        powerActive = 0;
        LPM1_EXIT;
    }

    P1IFG = 0;
}

// Timer interrupt, blink twice every few seconds..
#pragma vector=TIMERA0_VECTOR
__interrupt void timera0_isr(void)
{
    if (powerActive != 2)
        return;

    CCR0 = (++blinkState % 4) ? SHORTDELAY : LONGDELAY;

    if (blinkState % 2)
        P1OUT |= redLed;
    else
        P1OUT &= ~redLed;

    onTime += (blinkState % 4) ? 1 : 4;

    // Wake from sleep if coffeemaker has been on long enough
    if (onTime > POWERONTIME) {
        P1OUT &= ~redLed;
        powerActive = 0;
        LPM1_EXIT;
    }
}

{% endhighlight %}
