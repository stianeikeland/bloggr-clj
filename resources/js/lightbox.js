(function () {
  var links = document.querySelectorAll(
    'a[href$=".jpg"],a[href$=".jpeg"],a[href$=".JPG"],a[href$=".png"],a[href$=".gif"]',
  );
  for (var i = 0; i < links.length; i++) {
    var a = links[i];
    a.classList.add("lightbox");
    a.setAttribute("data-group", "page");
    var img = a.querySelector("img");
    if (img && img.alt) {
      a.setAttribute("data-caption", img.alt);
    }
  }
  if (links.length) {
    new Parvus();
  }
})();
