---
author: stianeikeland
comments: true
date: 2007-08-28 01:59:36+00:00
layout: post
slug: cell-sdk-on-ubuntu-feisty-fawn
title: Cell SDK on Ubuntu (Feisty Fawn).
wordpress_id: 41
tags:
- Linux
- Teknologi
---


    I ended up installing ubuntu on my playstation 3, pretty straight forward installation. Getting the Cell SDK up and running on ubuntu turned out to be a bit of a challenge however.  There's really not much information out there about how to do it, the closest I got was Gammel's "[Installing Cell SDK under Ubuntu](http://blog.gammal.org/2007/06/installing-cell-sdk-under-ubuntu.html)" - which almost worked. Most of the following is based on his recipe.

1. Install a few needed packages:



  
    
    $ apt-get install rpm freeglut3 freeglut3-dev libxmu-dev libxext-dev 
       build-essential perl rsync flex byacc tk8.4 tcl8.4 libelf1 gawk bash 
       libnetpbm10 libnuma1






2. Make sure sh points to bash:



  
    
    $ ls -la /bin/sh
    lrwxrwxrwx 1 root root 9 2007-08-25 07:33 /bin/sh -> /bin/bash






if it doesn't then replace it:



  
    
    $ rm  /bin/sh && ln -s /bin/bash /bin/sh






3. Replace mawk with gawk:



  
    
    $ update-alternatives --set awk /usr/bin/gawk






4. Add symlinks to a couple of libs:



  
    
    $ ln -s /usr/lib/libtcl8.4.so.0 /usr/lib/libtcl8.4.so
    $ ln -s /usr/lib/libtk8.4.so.0 /usr/lib/libtk8.4.so






5. [Download the Cell SDK](http://www.alphaworks.ibm.com/tech/cellsw/download?open&S_TACT=105AGX16&S_CMP=DWPA) (v2.1 is current version). Mount it and copy out the software.



  
    
    $ mkdir /media/cell && mount -o loop CellSDK21.iso /media/cell
    $ cp -r /media/cell/software /tmp/






6. Fix the install script, i needed to add --ignorearch since it complained about the system not beeing ppc64 for some reason. Then run it..



  
    
    $ cd /tmp/software
    $ sed -i 's/rpm -i/rpm -i --nodeps --ignorearch/g' cellsdk
    $ ./cellsdk install






7. Mount up the SPU-filesystem (if you get errors like "Unable to create SPE thread: Invalid argument" or "spu_create(): No such file or directory" while running your code this is probably why).



  
    
    $ mkdir /spu
    $ echo "none /spu spufs defaults 0 0" >> /etc/fstab
    $ mount -a





  
