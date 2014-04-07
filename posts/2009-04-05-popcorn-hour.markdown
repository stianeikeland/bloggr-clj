{ :slug "popcorn-hour"
  :title "Popcorn Hour"
  :date "2009-04-05 20:52:00+00:00"
  :tags #{}}

------

The Popcorn Hour is a relatively small networked mediaplayer running linux. Out of the box it's fanless, so the only source of noise is the added harddrive (it supports a SATA-drive or external USB drives), making it a good candidate for a quiet multimedia system. It can read most filesystems, but only has write-support for ext2/3. I put a 1 tb western digital green power drive in mine. On the output side it supports component, svideo, composite, hdmi 1.3a, analog and digital sound.

![Popcorn 1](http://s3.tadkom.net/wp-content/uploads/2009/04/imgp97692.jpg)

There are several ways to provide it with media, you can upload it via samba, ftp, nfs, etc.. etc.. stream it over the network from a variety of servers and shares, or simply plug it into your computer (it will act as a normal USB-disk). It's available on the network when you suspend it, so you can upload files to it at all times. And the good thing is, it basically plays everything you throw at it, including 1080p h264 mkv files. Sound is outputted either as the original stream (ac3, dts, aac) or as PCM over HDMI, and it works great with my receiver. It has a thriving community providing all sorts of cool hacks and the firmware people provide regular updates.

![Popcorn 2](http://s3.tadkom.net/wp-content/uploads/2009/04/ka911index2-300x168.jpg)

This could have been the holy grail of mediaplayers, but it has a few issues and annoyances. It's not future proof, if a new format shows up, then there's no guarantee that you can play it. The remote control sucks, you have to basically aim it directly at the unit for it to work (you can however use USB-input devices, so it would probably be possible to replace it, a keyboard works fine for example), maybe it would be possible to create an iPhone app to replace the remote. The user interface is slow and based on HTML. While being easy to customize, who decided it was a good idea to base a mediaplayer interface around a webbrowser?? The interfaces are quite static, no smooth transitions when moving around, and it's noticeably slow and laggy. Some settings I want to customize (for example buffer size when streaming) isn't possible.. If I could set the buffer size up to maybe 50 mb, then I could easily stream HD-media from my server at home.. It also no good for large music collections.

![Popcorn 3](http://s3.tadkom.net/wp-content/uploads/2009/04/ka911plot09-300x168.jpg)

There are several custom user interfaces showing up, I haven't really tried them out yet, but I suspect they are as slow as the original, but maybe more visually pleasing..  But all in all, it's small, portable, very low noise, very hackable, plays as good as everything.. and if you can live with the slow interface it's well worth the money. A full HTPC would probably be better, but this gives you like 95% of the features for 5% of the effort..

* [Networked Media Tank Wiki](http://www.networkedmediatank.com/wiki/index.php/Main_Page)
* [Networked media Tank Forum](http://www.networkedmediatank.com/index.php)
