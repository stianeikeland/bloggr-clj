(function () {
  var vids = document.querySelectorAll(
    'article iframe[src*="vimeo.com"],article iframe[src*="youtube.com"],article iframe[src*="youtube-nocookie.com"]',
  );
  for (var i = 0; i < vids.length; i++) {
    vids[i].style.width = "100%";
    vids[i].style.height = "auto";
    vids[i].style.aspectRatio = vids[i].getAttribute("width") + " / " + vids[i].getAttribute("height");
  }
})();
