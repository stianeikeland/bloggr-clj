---
author: stianeikeland
comments: true
date: 2012-10-01 22:24:53+00:00
layout: post
slug: the-shell-zsh-dotfiles-etc
title: The Shell - zsh, dotfiles, etc.
wordpress_id: 124
---

I decided to go for zsh again (instead of bash) as a default shell not that long ago. I've used it on and off before, it's really powerful, but takes a lot of configuration to get access to all the nice features it offers. Whenever i reinstalled or changed computers I usually just returned to the de-facto bash shell.

These days configuration/plugin/themes collections like [oh-my-zsh](https://github.com/robbyrussell/oh-my-zsh) and the more recent fork [prezto](https://github.com/sorin-ionescu/prezto) makes it really easy to get a a fully functional and good looking zsh up and running real quick. Git clone oh-my-zsh or prezto, activate the plugins you're after and you got it :)

<figure>
	<img src="/images/2012-10-01-the-shell/syntax.png">
</figure>

There are a few external plugins I really like as well - for example [zsh-syntax-highlighting](https://github.com/zsh-users/zsh-syntax-highlighting) seen above. Clone and add to plugin list as describes. It offers similar shell syntax highlighting as the fish shell.

Oh, and the theme used above is from [agnoster](https://gist.github.com/3712874). Pretty minimal while giving a lot of information - using the powerline fonts (a series of [patched fonts](https://github.com/Lokaltog/vim-powerline/wiki/Patched-fonts) - a favorite among many [vim users](https://github.com/Lokaltog/vim-powerline).) They've added pretty symbols to indicate things like branches, background jobs, root, etc. Looks a bit messy below, but that's mostly because i wanted to try to show as many features as possible in the least amount of space.

<figure>
	<img src="/images/2012-10-01-the-shell/powerline.png">
</figure>

Works really well in [iTerm2](http://www.iterm2.com/#/section/home). The terminal to use if you're on OS X.

I try to keep [my dotfiles on github](https://github.com/stianeikeland/dotfiles), that way it's pretty easy to move between machines and keep them up to date. Nice to have access to all your aliases and settings on the machines you're using. And whenever you're on a new mac, to avoid going mental, just apply your collection of [sane mac defaults](https://github.com/stianeikeland/dotfiles/blob/master/bin/sanemacdefaults.sh). ("natural" scrolling my ass :p) :)

There are some pretty awesome collections of dotfiles on github - some worth checking out: [holman's](https://github.com/holman/dotfiles), [mathias'](https://github.com/mathiasbynens/dotfiles) and even whole projects like: [dotfiles.github.com](http://dotfiles.github.com/)
