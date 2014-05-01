{ :slug "tools-of-the-trade"
  :title "Tools of the trade"
  :date "2014-05-01 09:00:00+01:00"
  :tags #{:tools :developer}}

------

Developers are the ultimate power users, we're really lazy and we hate to repeat ourselves. We'll spend hours automating 5 minute boring repetitive tasks - often wasting more time than it would take just to do it manually. Over the years we build up arsenals of useful tools that makes us more efficient, in this blog post I'll cover some of mine.

## [HTTPie](https://github.com/jkbr/httpie) - CRUD with style!

HTTPie is a small CLI tool for interacting with http-servers, it's very useful for debugging and testing out CRUD-like (REST, etc.) services. It's based on python, and can be installed using pip.

``` bash
$ http freegeoip.net/json/8.8.8.8
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
Content-Length: 186
Content-Type: application/json
Date: Wed, 23 Apr 2014 08:01:24 GMT

{
    "country_code": "US",
    "country_name": "United States",
    "ip": "8.8.8.8",
    "latitude": 38,
    "longitude": -97
}

$ http POST url.com/resource X-API-Token:xxxx urlparameter==5 name=value jsonfield:=jsonvalue morejson:=true
```

You can easily GET/POST/PUT/DELETE/etc values, and send headers, formfields, url parameters and json using the syntax shown above. Output is beautifully indented, and syntax highlighted.

## [ngrok](https://ngrok.com/) - access localhost from anywhere.

ngrok is a small tool you can use for breaking through firewalls. Say that you're working on a web project served up on localhost on your laptop, simply execute `ngrok <portnumber>` and ngrok will set up a tunnel to a hosted server and provide you with a public url. It also serves up a dashboard where you can show and inspect requests live, very useful for debugging. You can also use it to replay requests, for example while debugging something.

<figure class='half'>
<a href="/images/2014-04-tools-of-the-trade/ngrok2.png"><img src="/images/2014-04-tools-of-the-trade/ngrok2.png"></a>
<a href="/images/2014-04-tools-of-the-trade/ngrok1.png"><img src="/images/2014-04-tools-of-the-trade/ngrok1.png"></a>
</figure>

It can also be used for quick and easy tcp forwarding: `ngrok -proto=tcp 22` to expose your local ssh-server remotely.

It's open source (apache), and has a pay-what-you-want model for the hosted server. Most of the features can be used without signing up.

## [Vagrant](http://www.vagrantup.com/) - consistent dev environments.

Vagrant is tool for easily setting up virtual development environments. It acts as a wrapper around virtualbox, vmware or similar, and allows you to conveniently and consistently build, distribute and run virtual environments. Say that you're working on a project that requires go-lang, mongodb and nginx. You don't want to install and run all these services in your main environment, you want to isolate them and keep them contained with all their dependencies.

``` bash
$ cd projects/fizzbuzzer

# This will create a Vagrantfile (configuration file) in current dir
$ vagrant init precise64 http://files.vagrantup.com/precise64.box

$ cat Vagrantfile
VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "precise64"
  config.vm.box_url = "http://files.vagrantup.com/precise64.box"
end

# To run the VM, execute, it will download the base image if missing.
$ vagrant up
Bringing machine 'default' up with 'virtualbox' provider...
==> default: Booting VM...
==> default: Machine booted and ready!
==> default: Mounting shared folders...
    default: /vagrant => ~/projects/fizzbuzzer
...

# To access the VM, execute:
$ vagrant ssh
Welcome to Ubuntu 12.04 LTS (GNU/Linux 3.2.0-23-generic x86_64)
vagrant@precise64:~$

# Notice that vagrant has mounted your project directory as /vagrant
# This means that all your code/project files are available
# both inside and outside of the vm!
vagrant@precise64:~$ ls -l /vagrant/
-rw-r--r-- 1 vagrant vagrant 4626 Apr 23 08:41 Vagrantfile

# Stop the vm using, and up to start it again
$ vagrant halt
$ vagrant up

# Destroy the vm using:
$ vagrant destroy

```

If you run vagrant up now, it will build the vm from the base image again. This is awesome for saving space for projects that are put on the back burner. Vagrant images can be provisioned using chef, puppet or simple shell-scripts.

``` ruby
config.vm.provision :shell, :path => "vagrantbootstrap.sh"
```

Add the above line to Vagrantfile

``` bash
#!/bin/bash
echo I am provisioning...
apt-get update
apt-get -y upgrade
apt-get -y install golang mongodb nginx
date > /etc/vagrant_provisioned_at
```

And the above to vagrantbootstrap.sh

If you now execute `vagrant up`, it will set up a ubuntu base box, install the latest upgrades, and then install golang, mongodb and nginx. Check Vagrantfile and the provisioning script into git with the project source code, and your entire team has a consistent development environment - awesome!

Vagrant provides a metric ton of possibilities and you can easily do things complex multi-machine setups with different networking scenarios, check out the [manual](http://docs.vagrantup.com/v2/) for more information. [Vagrant 1.6](http://www.vagrantup.com/blog/feature-preview-vagrant-1-6-windows.html) will also add support for running Windows guests - pretty cool! Provisioning is done via WinRM, and you get easy access to desktop via `$ vagrant rdp` and synced folders also work.

## [GNU parallel](https://www.gnu.org/software/parallel/) - most overlooked useful GNU-util?

This is a overlooked powerhouse that deserves to be used. You can think of it kinda like xargs on steroids. Everytime you do something with a shell for-loop, or hack something together with xargs, gnu parallel can probably do it better.

``` bash
# Don't do this
for i in `cat ./file.txt`; do
    buzzfizz $i
done

# Do this instead
$ parallel -a file.txt buzzfizz
```
Doesn't that seem easier? But wait, you can do so much more..

``` bash
# All combinations of two inputs
$ parallel echo ::: A B C ::: 1 2 3
C 1
C 2
B 3
B 2
B 1
A 3
A 2
A 1
C 3

# Map over two inputs
$ parallel --xapply echo ::: A B C ::: 1 2 3
B 2
A 1
C 3

# Resize images in parallel (8 jobs) using imagemagick
$ parallel -j 8 convert {} -resize 800x600 {.}_small.jpg ::: *.jpg

# Run (same) tasks on multiple machines via SSH
$ parallel --onall -S serverA,serverB ping -c 5 ::: vg.no db.no

# Distributed image resizing by transfering the files to multiple servers (A,B)
# run imagemagick and transfer back artifacts.
$ parallel -S A,B -j 2 --transfer --return {.}_small.jpg convert {} -resize 640x480 {.}_small.jpg ::: *.jpg
```

Install it on mac using homebrew: `brew install parallel`

## [pv](https://code.google.com/p/pipeviewer/‎) - Pipe Viewer

``` bash
$ pv -cN read *.gz | gunzip | pv -cN unpack | gzip | pv -cN pack > /dev/null
     read: 78.2MiB 0:00:09 [8.64MiB/s] [=======>          ] 43% ETA 0:00:05
   unpack:  258MiB 0:00:09 [27.3MiB/s] [     <=>          ]
     pack: 77.8MiB 0:00:09 [8.62MiB/s] [     <=>          ]
```

Monitor data as it flows through pipes, just put it between two processes to see traffic stats.

## [http-server](https://npmjs.org/package/http-server) (npm/node) - instant web server in local directory.

Launching a webserver in the local directory for ad-hoc tasks is very useful, and python is pretty awesome for this `python -m SimpleHTTPServer` or `python -m http.server` (for python v3). There's only one problem - it's slow, single-threaded/blocking (modern multi-file js heavy project can give weird errors), and will crash on big files (try serving up a movie to an iPad or similar).

If you have a node-stack set up, then http-server is a good alternative. `npm install http-server -g`

~~~ bash
$ http-server
Starting up http-server, serving ./ on port: 8080
Hit CTRL-C to stop the server
[Mon, 28 Apr 2014 14:51:35 GMT] "GET /" "Mozilla/5.0 ..."
[Mon, 28 Apr 2014 14:51:35 GMT] "GET /favicon.ico" "Mozilla/5.0 ..."
~~~

I use this for so many different tasks, like serving up files during development, for moving files to other computers, for streaming movies on tablets or other computers, etc.. etc..

## [Dash](http://kapeli.com/dash) - Documentation browser for Mac

Dash is a documentation browser and code snippet manager, allowing you to search documentation while offline. It will download (and keep up to date) documentation from a wide array of languages and frameworks (you select from a list). It integrates with most editors.

<figure>
<a href="/images/2014-04-tools-of-the-trade/dash.png"><img src="/images/2014-04-tools-of-the-trade/dash.png"></a>
</figure>

I do not use the snippet functionality, but the documentation browser / search ability is pretty decent, especially if you're trying to work offline. Just load up on all the languages and frameworks you normally use.

## [Alfred](http://www.alfredapp.com/) - App launcher

Alfred is a app launcher for OS X, with loads of plugins available.

<figure class='half'>
<a href="/images/2014-04-tools-of-the-trade/alfred-vm.png"><img src="/images/2014-04-tools-of-the-trade/alfred-vm.png"></a>
<a href="/images/2014-04-tools-of-the-trade/alfred-tz.png"><img src="/images/2014-04-tools-of-the-trade/alfred-tz.png"></a>
</figure>

Hide that stupid dock and save some valuable screen estate. I use alfred for:

* Launching apps.
* Locking screen.
* Starting/Stopping virtual machines.
* Umounting usb drives / network mounts.
* Killing processes.
* Quickly searching for something on amazon, ebay, wikipedia, etc.
* Looking up contacts.
* Calculator.
* Currency conversions.
* Open current finder folder in iTerm.
* Searching for files.
* etc....

There's a list of (potentially) useful Alfred plugins here: [github/zenorocha/alfred-workflows](https://github.com/zenorocha/alfred-workflows).

## [SnappyApp](http://www.snappy-app.com/) - Floating screenshots

You know when you're coding on a small laptop screen, and constantly {alt/cmd}-tab to some reference documentation in your browser - isn't that annoying?

<figure>
<a href="/images/2014-04-tools-of-the-trade/snappy.png"><img src="/images/2014-04-tools-of-the-trade/snappy.png"></a>
<figcaption>Editor and floating reference documentation.</figcaption>
</figure>

Hit a keyboard shortcut and snappy app takes a screenshot of the part of the screen you're interested in. It floats on top, and you can move it around. Double-click to dismiss.
