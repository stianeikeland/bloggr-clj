---
author: stianeikeland
comments: true
date: 2009-04-05 08:58:00+00:00
layout: post
slug: illustrative-volume-rendering
title: Illustrative Volume Rendering
wordpress_id: 26
tags:
- ct
- glsl
- opengl
- shader
- Teknologi
- UiB
- visualization
---


    

This semester I've been doing a bit of shader programming (GLSL), and have implemented a series of illustrative techniques for volume rendering. The goal was to simulate some of the techniques used by illustrators and apply them to volumetric data (such as a CT-scan).  The first is a CT-dataset rendered using a classic cartoon-shader, it also includes curvature-controlled contours:  


![](http://s3.tadkom.net/wp-content/uploads/2009/04/volumeshop-2009-03-01-0077-viewer-300x272.png)



 Same dataset using gooch-shading, includes gooch-splashback and contours:  


![](http://s3.tadkom.net/wp-content/uploads/2009/04/volumeshop-2009-03-01-0058-viewer-260x300.png)



 Last is a CT-scan of a gecko, using different contour techniques - curvature controlled for the edges, and a low exponent gradient curvature for a bit of shading near the edges.. Looks pretty close to something one might draw on a piece of paper.  


![](http://s3.tadkom.net/wp-content/uploads/2009/04/volumeshop-2009-03-01-0037-viewer-300x253.png)



 Oh, and it's all done in real time.. I love GPUs.. More of my illustravis stuff can be seen at: [http://www.student.uib.no/~sei081/visualization/illust/](http://www.student.uib.no/~sei081/visualization/illust/)


  
