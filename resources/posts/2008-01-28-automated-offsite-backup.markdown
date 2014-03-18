---
author: stianeikeland
comments: true
date: 2008-01-28 02:46:00+00:00
layout: post
slug: automated-offsite-backup
title: Automated offsite backup.
wordpress_id: 37
tags:
- Linux
---


    

In order to sleep better at night I've decided to be better at taking backups of important data. Knowing myself it would need to be automated, and also preferably offsite. Also didn't want to depend on my own servers (since they are down from time to time, lately I've been fighting an ethernet switch that stops working until someone cycles the power.. very annoying, it's getting replaced now..).  I had a look at amazon S3, which seems to be fairly popular these days. $0.15 per gb per month of storage, and a little bit for bandwidth. That's basically free for my storage needs, especially considering the current dollar value (when are they going to rename it american pesos?).  Sounds great, cheap offsite backup. However I want to encrypt my data, since I don't trust anyone further than I can throw them in matters like this, and it's hard to throw someone the size of amazon..  I had a look around and found duplicity, which does encrypted incremental backups. It's made in python and uses librsync, it supports loads of different destinations - for example scp, rsync, ftp and even S3!  So I installed the newest version of [duplicity](http://duplicity.nongnu.org/) and it's dependencies ( [boto](http://code.google.com/p/boto/) - for S3 support, the rest can be found in most distributions ) and also made a GPG-key specifically for backup use (gpg --gen-key).  And made a little script:





  
    
    #!/bin/bash
    
    # Amazon S3 keys:
    export AWS_ACCESS_KEY_ID="XXXXXXXXXXXXXXXXXX"
    export AWS_SECRET_ACCESS_KEY="XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    
    # GPG passphrase and key:
    export PASSPHRASE=XXXXXXXX
    GPG_KEY=XXXXXXXX
    
    # MYSQL password:
    MYSQLPW=XXXXXXXX
    
    DATE=`date +%d`
    
    SOURCE=/
    DEST="s3+http://nameofyourbackupbucket"
    
    mysqldump --all-databases --password=$MYSQLPW > /root/mysql/mysql-backup.sql
    
    # Force a full backup twice a month..
    
    if (( "$DATE" % 15 == "0" )) ; then
    
            duplicity full 
                    --include-globbing-filelist /root/backuplist.txt 
                    --encrypt-key "$GPG_KEY" 
                    --sign-key "$GPG_KEY" 
                    --exclude=/** 
                    $SOURCE $DEST
    
            # Don't really need more than 2 months of backup..
            duplicity remove-older-than 2M $DEST
    
    else 
    
            duplicity 
                    --include-globbing-filelist /root/backuplist.txt 
                    --encrypt-key "$GPG_KEY" 
                    --sign-key "$GPG_KEY" 
                    --exclude=/** 
                    $SOURCE $DEST
    
    fi
    
    export AWS_ACCESS_KEY_ID=
    export AWS_SECRET_ACCESS_KEY=
    export PASSPHRASE=








Everything i want to backup is listed in the file backuplist.txt, and the script runs once every night using cron.... This should cover my backup needs, if I only remember to backup my GPG-key :p


  
