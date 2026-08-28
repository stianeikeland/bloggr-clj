(function () {
  var nav = document.getElementById("site-nav");
  if (!nav) return;

  nav.insertAdjacentHTML(
    "afterbegin",
    '<button type="button" id="menutoggle" class="nav-toggle" aria-expanded="false" aria-controls="site-nav"><span class="lines"></span>menu</button>',
  );

  var button = document.getElementById("menutoggle");

  button.addEventListener("click", function () {
    var open = !button.classList.contains("active");
    button.classList.toggle("active");
    button.setAttribute("aria-expanded", open ? "true" : "false");
  });

  document.addEventListener("click", function (e) {
    var mobile = getComputedStyle(button).display !== "none";
    if (mobile && button.classList.contains("active") && !button.contains(e.target)) {
      close();
    }
  });

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && button.classList.contains("active")) {
      close();
      button.focus();
    }
  });

  function close() {
    button.classList.remove("active");
    button.setAttribute("aria-expanded", "false");
  }
})();
