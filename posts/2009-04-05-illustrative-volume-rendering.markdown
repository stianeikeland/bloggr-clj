{ :slug "illustrative-volume-rendering"
  :title "Illustrative Volume Rendering"
  :date "2009-04-05 08:58:00+00:00"
  :tags #{:visualization}}

------

This semester I've been doing a bit of shader programming (GLSL), and have implemented a series of illustrative techniques for volume rendering. The goal was to simulate some of the techniques used by illustrators and apply them to volumetric data (such as a CT-scan).  The first is a CT-dataset rendered using a classic cartoon-shader, it also includes curvature-controlled contours:

<figure>
  <a href="/images/visualization/head1.jpg"><img src="/images/visualization/head1.jpg" alt=""></a>
</figure>

<figure>
  <a href="/images/visualization/head2.jpg"><img src="/images/visualization/head2.jpg" alt=""></a>
</figure>

 Last is a CT-scan of a gecko, using different contour techniques - curvature controlled for the edges, and a low exponent gradient curvature for a bit of shading near the edges.. Looks pretty close to something one might draw on a piece of paper.

<figure>
  <a href="/images/visualization/gecko2.jpg"><img src="/images/visualization/gecko2.jpg" alt=""></a>
</figure>

<figure>
  <a href="/images/visualization/gecko1.jpg"><img src="/images/visualization/gecko1.jpg" alt=""></a>
</figure>

<figure>
  <a href="/images/visualization/beetle.jpg"><img src="/images/visualization/beetle.jpg" alt=""></a>
</figure>

Oh, and it's all done in real time.. I love GPUs.. More of my illustravis stuff can be seen at: [uib.no](http://www.ii.uib.no/vis/teaching/vis-special/2009-spring/eikeland/CA1.html)



