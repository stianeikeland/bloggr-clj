---
author: stianeikeland
comments: true
date: 2013-07-25 21:44:54+00:00
layout: post
slug: openwrt-on-hama-mpr-a1-v2-2
title: OpenWRT on Hama MPR-A1 (v2.2)
wordpress_id: 198
image:
  feature: 2013-07-25-openwrt-on-hama-mpr-a1-v2-2/4.jpg
tags:
- hacking
- mpr-a1
- openwrt
- router
---

<figure style="float: right; margin-left: 15px;">
  <img src="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/201204160226342556.jpg">
</figure>

Hama MPR-A1 is a tiny pocket router (ralink 5350f based, 360 mhz, 4 mb flash, 16 mb ram) that features a 1800 mah built in battery. I bought one on ebay for about $20 and wanted to get OpenWRT running on it. You can find patches for openwrt on 5350 here: [OpenWrt-RT5350](https://github.com/Squonk42/OpenWrt-RT5350)

You need a uart connection to flash this thing, so open it up and remove the battery from the backside of the PCB.

Apparently there are multiple revisions of MPR-A1 around, and mine was a bit different from the others I've been able to find on the internet. Mine had a layer of copper foil covering the PCB, I pealed it away to locate the RX/TX uart connections. Sadly, on my revision (2.2), the pads are covered with solder mask - I do not know if they are made this way now, or if mine was from a weird batch.

<!-- [gallery columns="4" type="square" ids="200,201,202,203"] -->

<figure class="third">
  <a href="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/1.jpg">
    <img src="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/1.jpg">
  </a>
  <a href="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/2.jpg">
    <img src="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/2.jpg">
  </a>
  <a href="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/3.jpg">
    <img src="/images/2013-07-25-openwrt-on-hama-mpr-a1-v2-2/3.jpg">
  </a>
</figure>

I sanded down the solder mask to reveal the copper pads, and soldered RX, TX and GND to a UART usb adapter (from an old nokia connector in my case). Be careful not to sand too much, the copper is pretty thin.

Next, prepare a suitable openwrt image, and set up a tftp server. On a mac it's pretty easy and already built in, simply put the firmware in /private/tftpboot - to start the tftp daemon run:

~~~ bash
sudo launchctl load -F /System/Library/LaunchDaemons/tftp.plist
sudo launchctl start com.apple.tftpd
# When done, substitute with unload and stop
~~~

Next, make a serial connection, screen is pretty nice for this on a mac: screen /dev/tty.usbserial 57600 ..
Power up the router and hold down '2' .. (uboot will give you some boot options, 2 = flash from tftp). Connect via ethernet to the router and follow the tftp instructions.

Voila, OpenWRT :)

This device only has 16 mb of ram and would probably need a ram upgrade to be useful with openwrt. Running luci is insanely slow, so slow that it will drive most people mad. I would rather recommend the [TP-Link TL-MR10U](http://wiki.openwrt.org/toh/tp-link/tl-mr10u). It's a similar device in the same price range, basically a wr703n with battery - meaning atheros based and 32 mb ram. I didn't know about the TP-Link at the time when I bought this.


~~~ bash
============================================
Ralink UBoot Version: 3.6.0.0
--------------------------------------------
ASIC 5350_MP (Port5<->None)
DRAM_CONF_FROM: Boot-Strapping
DRAM_TYPE: SDRAM
DRAM_SIZE: 128 Mbits
DRAM_WIDTH: 16 bits
DRAM_TOTAL_WIDTH: 16 bits
TOTAL_MEMORY_SIZE: 16 MBytes
Flash component: SPI Flash
Date:Dec 13 2011  Time:13:49:42
============================================
icache: sets:256, ways:4, linesz:32 ,total:32768
dcache: sets:128, ways:4, linesz:32 ,total:16384

 ##### The CPU freq = 360 MHZ ####
 estimate memory size =16 Mbytes
raspi_read: from:40028 len:6
.
raspi_read: from:0 len:30004
....*************Is_update = 0 plat = 1**************

Please choose the operation:
   1: Load system code to SDRAM via TFTP.
   2: Load system code then write to Flash via TFTP.
   3: Boot system code via Flash (default).
   4: Entr boot command line interface.
   7: Load Boot Loader code then write to Flash via Serial.
   9: Load Boot Loader code then write to Flash via TFTP.

You choosed 2
                                                                                                                                                                                                                                          0

2: System Load Linux Kernel then write to Flash via TFTP.
 Warning!! Erase Linux in Flash then burn new one. Are you sure?(Y/N)
 Please Input new ones /or Ctrl-C to discard
        Input device IP (192.168.1.109) ==:10.0.0.2
        Input server IP (192.168.1.55) ==:10.0.0.1
        Input Linux Kernel filename () ==:mpr-a1-16m-luci-usb-mjpg.bin

 netboot_common, argc= 3

 NetTxPacket = 0x80FE6AC0

 KSEG1ADDR(NetTxPacket) = 0xA0FE6AC0

 NetLoop,call eth_halt !

 NetLoop,call eth_init !
Trying Eth0 (10/100-M)

 Waitting for RX_DMA_BUSY status Start... done

 Header Payload scatter function is Disable !!

 ETH_STATE_ACTIVE!!
Using Eth0 (10/100-M) device
TFTP from server 10.0.0.1; our IP address is 10.0.0.2
Filename 'mpr-a1-16m-luci-usb-mjpg.bin'.

 TIMEOUT_COUNT=10,Load address: 0x80100000
Loading: Got ARP REPLY, set server/gtwy eth addr (40:6c:8f:39:df:18)
Got it
#################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         #################################################################
         ##########################################
done
Bytes transferred = 3538948 (360004 hex)
NetBootFileXferSize= 00360004
raspi_erase_write: offs:50000, count:360004
raspi_erase: offs:50000 len:360000
......................................................
raspi_write: to:50000 len:360000
......................................................
raspi_read: from:50000 len:10000
.raspi_read: from:60000 len:10000
.raspi_read: from:70000 len:10000
.raspi_read: from:80000 len:10000
.raspi_read: from:90000 len:10000
.raspi_read: from:a0000 len:10000
.raspi_read: from:b0000 len:10000
.raspi_read: from:c0000 len:10000
.raspi_read: from:d0000 len:10000
.raspi_read: from:e0000 len:10000
.raspi_read: from:f0000 len:10000
.raspi_read: from:100000 len:10000
.raspi_read: from:110000 len:10000
.raspi_read: from:120000 len:10000
.raspi_read: from:130000 len:10000
.raspi_read: from:140000 len:10000
.raspi_read: from:150000 len:10000
.raspi_read: from:160000 len:10000
.raspi_read: from:170000 len:10000
.raspi_read: from:180000 len:10000
.raspi_read: from:190000 len:10000
.raspi_read: from:1a0000 len:10000
.raspi_read: from:1b0000 len:10000
.raspi_read: from:1c0000 len:10000
.raspi_read: from:1d0000 len:10000
.raspi_read: from:1e0000 len:10000
.raspi_read: from:1f0000 len:10000
.raspi_read: from:200000 len:10000
.raspi_read: from:210000 len:10000
.raspi_read: from:220000 len:10000
.raspi_read: from:230000 len:10000
.raspi_read: from:240000 len:10000
.raspi_read: from:250000 len:10000
.raspi_read: from:260000 len:10000
.raspi_read: from:270000 len:10000
.raspi_read: from:280000 len:10000
.raspi_read: from:290000 len:10000
.raspi_read: from:2a0000 len:10000
.raspi_read: from:2b0000 len:10000
.raspi_read: from:2c0000 len:10000
.raspi_read: from:2d0000 len:10000
.raspi_read: from:2e0000 len:10000
.raspi_read: from:2f0000 len:10000
.raspi_read: from:300000 len:10000
.raspi_read: from:310000 len:10000
.raspi_read: from:320000 len:10000
.raspi_read: from:330000 len:10000
.raspi_read: from:340000 len:10000
.raspi_read: from:350000 len:10000
.raspi_read: from:360000 len:10000
.raspi_read: from:370000 len:10000
.raspi_read: from:380000 len:10000
.raspi_read: from:390000 len:10000
.raspi_read: from:3a0000 len:10000
.raspi_read: from:3b0000 len:10000
.raspi_erase: offs:3b0000 len:10000
.
raspi_write: to:3b0000 len:10000
.
raspi_read: from:3b0000 len:10000
.Done!
## Booting image at bc050000 ...
raspi_read: from:50000 len:40
.   Image Name:   MIPS OpenWrt Linux-3.7.5
   Created:      2013-04-11  15:04:48 UTC
   Image Type:   MIPS Linux Kernel Image (lzma compressed)
   Data Size:    915870 Bytes = 894.4 kB
   Load Address: 80000000
   Entry Point:  80000000
raspi_read: from:50040 len:df99e
..............   Verifying Checksum ... OK
   Uncompressing Kernel Image ... OK
No initrd
## Transferring control to Linux (at address 80000000) ...
## Giving linux memsize in MB, 16

Starting kernel ...

[    0.000000] Linux version 3.7.5 (lich@lich-pc) (gcc version 4.6.4 20121210 (prerelease) (Linaro GCC 4.6-2012.12) ) #1 Thu Apr 11 23:04:28 CST 2013
[    0.000000] bootconsole [early0] enabled
[    0.000000] CPU revision is: 0001964c (MIPS 24KEc)
[    0.000000] Ralink RT5350 id:1 rev:3 running at 360.00 MHz
[    0.000000] Determined physical RAM map:
[    0.000000]  memory: 01000000 @ 00000000 (usable)
[    0.000000] User-defined physical RAM map:
[    0.000000]  memory: 01000000 @ 00000000 (usable)
[    0.000000] Initrd not found or empty - disabling initrd
[    0.000000] Zone ranges:
[    0.000000]   Normal   [mem 0x00000000-0x00ffffff]
[    0.000000] Movable zone start for each node
[    0.000000] Early memory node ranges
[    0.000000]   node   0: [mem 0x00000000-0x00ffffff]
[    0.000000] Primary instruction cache 32kB, VIPT, 4-way, linesize 32 bytes.
[    0.000000] Primary data cache 16kB, 4-way, VIPT, no aliases, linesize 32 bytes
[    0.000000] Built 1 zonelists in Zone order, mobility grouping off.  Total pages: 4064
[    0.000000] Kernel command line:  board=MPR-A1 console=ttyS1,57600 mtdparts=spi0.0:192k(u-boot)ro,64k(u-boot-env)ro,64k(factory)ro,896k(kernel),2880k(rootfs),3776k@0x50000(firmware) rootfstype=squashfs,jffs2 mem=16M
[    0.000000] PID hash table entries: 64 (order: -4, 256 bytes)
[    0.000000] Dentry cache hash table entries: 2048 (order: 1, 8192 bytes)
[    0.000000] Inode-cache hash table entries: 1024 (order: 0, 4096 bytes)
[    0.000000] __ex_table already sorted, skipping sort
[    0.000000] Writing ErrCtl register=00000000
[    0.000000] Readback ErrCtl register=00000000
[    0.000000] Memory: 13388k/16384k available (1969k kernel code, 2996k reserved, 473k data, 180k init, 0k highmem)
[    0.000000] SLUB: Genslabs=9, HWalign=32, Order=0-3, MinObjects=0, CPUs=1, Nodes=1
[    0.000000] NR_IRQS:48
[    0.000000] console [ttyS1] enabled, bootconsole disabled
[    0.000000] console [ttyS1] enabled, bootconsole disabled
[    0.010000] Calibrating delay loop... 239.61 BogoMIPS (lpj=1198080)
[    0.080000] pid_max: default: 32768 minimum: 301
[    0.080000] Mount-cache hash table entries: 512
[    0.090000] NET: Registered protocol family 16
[    0.100000] MIPS: machine is HAME MPR-A1
[    0.130000] bio: create slab <bio-0> at 0
[    0.140000] Switching to clocksource MIPS
[    0.150000] NET: Registered protocol family 2
[    0.160000] TCP established hash table entries: 512 (order: 0, 4096 bytes)
[    0.180000] TCP bind hash table entries: 512 (order: -1, 2048 bytes)
[    0.190000] TCP: Hash tables configured (established 512 bind 512)
[    0.200000] TCP: reno registered
[    0.210000] UDP hash table entries: 256 (order: 0, 4096 bytes)
[    0.220000] UDP-Lite hash table entries: 256 (order: 0, 4096 bytes)
[    0.230000] NET: Registered protocol family 1
[    0.280000] squashfs: version 4.0 (2009/01/31) Phillip Lougher
[    0.290000] jffs2: version 2.2 (NAND) (SUMMARY) (LZMA) (RTIME) (CMODE_PRIORITY) (c) 2001-2006 Red Hat, Inc.
[    0.310000] msgmni has been set to 26
[    0.320000] io scheduler noop registered
[    0.330000] io scheduler deadline registered (default)
[    0.340000] Serial: 8250/16550 driver, 2 ports, IRQ sharing disabled
[    0.360000] serial8250: ttyS0 at MMIO 0x10000500 (irq = 13) is a 16550A
[    0.370000] serial8250: ttyS1 at MMIO 0x10000c00 (irq = 20) is a 16550A
[    0.390000] ramips-spi ramips-spi.0: master is unqueued, this is deprecated
[    0.410000] m25p80 spi0.0: pm25lq032 (4096 Kbytes)
[    0.410000] 6 cmdlinepart partitions found on MTD device spi0.0
[    0.430000] Creating 6 MTD partitions on "spi0.0":
[    0.440000] 0x000000000000-0x000000030000 : "u-boot"
[    0.450000] 0x000000030000-0x000000040000 : "u-boot-env"
[    0.460000] 0x000000040000-0x000000050000 : "factory"
[    0.480000] 0x000000050000-0x000000130000 : "kernel"
[    0.490000] 0x000000130000-0x000000400000 : "rootfs"
[    0.510000] mtd: partition "rootfs" set to be root filesystem
[    0.520000] mtd: partition "rootfs_data" created automatically, ofs=380000, len=80000
[    0.530000] 0x000000380000-0x000000400000 : "rootfs_data"
[    0.550000] 0x000000050000-0x000000400000 : "firmware"
[    0.570000] ramips-wdt ramips-wdt: timeout value must be 0 < timeout <= 35, using 35
[    0.590000] TCP: cubic registered
[    0.600000] NET: Registered protocol family 17
[    0.600000] 8021q: 802.1Q VLAN Support v1.8
[    0.640000] VFS: Mounted root (squashfs filesystem) readonly on device 31:4.
[    0.650000] Freeing unused kernel memory: 180k freed
[    5.090000] input: gpio-keys-polled as /devices/platform/gpio-keys-polled/input/input0
[    5.190000] Button Hotplug driver version 0.4.1
- preinit -
Press the [f] key and hit [enter] to enter failsafe mode
- regular preinit -
jffs2 not ready yet; using ramdisk
- init -
Please press Enter to activate this console.
[   12.230000] Compat-drivers backport release: compat-drivers-2013-01-08-3
[   12.250000] Backport based on wireless-testing.git master-2013-01-07
[   12.260000] compat.git: wireless-testing.git
[   12.300000] cfg80211: Calling CRDA to update world regulatory domain
[   12.320000] cfg80211: World regulatory domain updated:
[   12.330000] cfg80211:   (start_freq - end_freq @ bandwidth), (max_antenna_gain, max_eirp)
[   12.340000] cfg80211:   (2402000 KHz - 2472000 KHz @ 40000 KHz), (300 mBi, 2000 mBm)
[   12.360000] cfg80211:   (2457000 KHz - 2482000 KHz @ 20000 KHz), (300 mBi, 2000 mBm)
[   12.380000] cfg80211:   (2474000 KHz - 2494000 KHz @ 20000 KHz), (300 mBi, 2000 mBm)
[   12.390000] cfg80211:   (5170000 KHz - 5250000 KHz @ 40000 KHz), (300 mBi, 2000 mBm)
[   12.410000] cfg80211:   (5735000 KHz - 5835000 KHz @ 40000 KHz), (300 mBi, 2000 mBm)
[   13.440000] usbcore: registered new interface driver usbfs
[   13.450000] usbcore: registered new interface driver hub
[   13.460000] usbcore: registered new device driver usb
[   14.640000] PPP generic driver version 2.4.2
[   14.940000] ip_tables: (C) 2000-2006 Netfilter Core Team
[   15.130000] NET: Registered protocol family 24
[   15.160000] ehci_hcd: USB 2.0 'Enhanced' Host Controller (EHCI) Driver
[   16.170000] ehci-platform ehci-platform: Generic Platform EHCI Controller
[   16.190000] ehci-platform ehci-platform: new USB bus registered, assigned bus number 1
[   16.230000] ehci-platform ehci-platform: irq 26, io mem 0x101c0000
[   16.260000] ehci-platform ehci-platform: USB 2.0 started, EHCI 1.00
[   16.270000] hub 1-0:1.0: USB hub found
[   16.280000] hub 1-0:1.0: 1 port detected
[   16.310000] nf_conntrack version 0.5.0 (212 buckets, 848 max)
[   16.640000] ohci_hcd: USB 1.1 'Open' Host Controller (OHCI) Driver
[   16.660000] ohci-platform ohci-platform: Generic Platform OHCI Controller
[   16.670000] ohci-platform ohci-platform: new USB bus registered, assigned bus number 2
[   16.690000] ohci-platform ohci-platform: irq 26, io mem 0x101c1000
[   16.760000] hub 2-0:1.0: USB hub found
[   16.770000] hub 2-0:1.0: 1 port detected
[   16.840000] usbcore: registered new interface driver cdc_acm
[   16.850000] cdc_acm: USB Abstract Control Model driver for USB modems and ISDN adapters
[   17.030000] usbcore: registered new interface driver usbserial
[   17.040000] usbcore: registered new interface driver usbserial_generic
[   17.050000] usbserial: USB Serial support registered for generic
[   17.210000] Linux video capture interface: v2.00
[   17.390000] usbcore: registered new interface driver ftdi_sio
[   17.410000] usbserial: USB Serial support registered for FTDI USB Serial Device
[   17.510000] gspca_main: v2.14.0 registered
[   17.540000] usbcore: registered new interface driver gspca_zc3xx
[   17.580000] usbcore: registered new interface driver uvcvideo
[   17.590000] USB Video Class driver (1.1.1)
[   26.430000] device eth0.1 entered promiscuous mode
[   26.440000] device eth0 entered promiscuous mode
[   26.460000] br-lan: port 1(eth0.1) entered forwarding state
[   26.470000] br-lan: port 1(eth0.1) entered forwarding state
[   28.080000] ramips-wdt: timeout value 60 must be 0 < timeout <= 35, using 35
[   28.470000] br-lan: port 1(eth0.1) entered forwarding state
[   48.100000] jffs2_scan_eraseblock(): End of filesystem marker found at 0x0
[   48.140000] jffs2_build_filesystem(): unlocking the mtd device... done.
[   48.150000] jffs2_build_filesystem(): erasing all blocks after the end marker... done.
[   51.360000] jffs2: notice: (1075) jffs2_build_xattr_subsystem: complete building xattr subsystem, 0 of xdatum (0 unchecked, 0 orphan) and 0 of xref (0 dead, 0 orphan) found.

BusyBox v1.19.4 (2013-04-11 22:01:38 CST) built-in shell (ash)
Enter 'help' for a list of built-in commands.

  _______                     ________        __
 |       |.-----.-----.-----.|  |  |  |.----.|  |_
 |   -   ||  _  |  -__|     ||  |  |  ||   _||   _|
 |_______||   __|_____|__|__||________||__|  |____|
          |__| W I R E L E S S   F R E E D O M
 -----------------------------------------------------
 BARRIER BREAKER (Bleeding Edge, r35407)
 -----------------------------------------------------
  * 1/2 oz Galliano         Pour all ingredients into
  * 4 oz cold Coffee        an irish coffee mug filled
  * 1 1/2 oz Dark Rum       with crushed ice. Stir.
  * 2 tsp. Creme de Cacao
 -----------------------------------------------------
root@OpenWrt:~#
~~~


