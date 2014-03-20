---
author: stianeikeland
comments: true
date: 2007-09-05 22:57:26+00:00
layout: post
slug: asterisk-resolve-callerid-using-online-phone-directories
title: Asterisk - Resolve callerid using online phone directories.
wordpress_id: 40
tags:
- Linux
- Teknologi
---


    Here's how to automatically resolve incoming callerids using a public online phone directory with a bit of PHP.

First you need a few packages installed.. php(4/5)-cli and php(4/5)-curl.. Copy the following code and save it as a file ('resolvnum.agi') in your agi-bin directory, most likely /usr/share/asterisk/agi-bin. Make sure it's executable: chmod +rx resolvnum.agi



  
    
    #!/usr/bin/php -q
    <?php
    
    // Do not wait more than 4 secs..
    ob_implicit_flush(false);
    set_time_limit(5);
    
    $stdin = fopen("php://stdin", "r");
    $stdout = fopen("php://stdout", "w");
    
    // Get all agi-variables from asterisk via stdin:
    while (!feof($stdin)) {
            $temp = fgets($stdin);
            $temp = str_replace("n", "", $temp);
            $s = explode(":", $temp);
            $agivar[$s[0]] = trim($s[1]);
    
            if (($temp == "") || ($temp == "n"))
                    break;
    }
    
    // Get callerid from agi-variables.
    $callerid = $agivar['agi_callerid'];
    
    if (!is_numeric($callerid)) exit(1);
    
    // Set up curl:
    $user_agent = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)";
    $ch = @curl_init("http://www.kvasir.no/telefonkatalog/searchresult.html?q=".$callerid."&x=0&y=0/");
    
    @curl_setopt($ch, CURLOPT_USERAGENT, $user_agent);
    @curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
    @curl_setopt($ch, CURLOPT_TIMEOUT,4);
    
    // Fetch us some data...
    $html = str_replace("r", "", @curl_exec($ch));
    curl_close($ch);
    
    // Got nothing? Oh, bother...
    if (!$html) exit(0);
    
    // Replace Norwegian characters..
    $html = str_replace(array(chr(230), chr(248), chr(229), chr(198), chr(216), chr(197)),
                     array("ae", "oe", "aa", "AE", "OE", "AA"), $html);
    
    // Try to match some names..
    preg_match_all("|<h4 class="forste_linje">n(.*)n</h4>|U", $html, $names, PREG_SET_ORDER);
    
    // Yay, we found something, set calleridname.. leave number alone..
    if (isset($names[0][1])) {
            fputs($stdout,"SET CALLERID "".$names[0][1]." (".$callerid.") <".$callerid.">"n");
            fflush($stdout);
    }
    
    exit(0);
    ?>






Then add the agi to your incoming extensions in extensions.conf. Mine is set up like this:



  
    
    [from-sip]
    exten => 85XXXXXX,1,AGI(nummeroppslag.agi)
    exten => 85XXXXXX,2,Macro(dialer,101,SIP/101&SIP/102,101)
    exten => 85XXXXXX,3,Hangup






It should now query [www.kvasir.no/telefonkatalogen](http://www.kvasir.no/telefonkatalogen) and set the callerid-name (which will show up on your phone) if it finds something. Since I'm pretty sure that kvasir won't like this (I take no responsibility for your usage), I would recommend implementing a cache if you have a lot of traffic. You can also easily adapt it to other phone directories by changing the search URL and regexp.
  
