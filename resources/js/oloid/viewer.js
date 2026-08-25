(function () {
  var container = document.getElementById('stl-viewer');
  if (!container || !window.THREE || !THREE.STLLoader || !THREE.OrbitControls) return;

  var height = 480;
  var renderer;
  try {
    renderer = new THREE.WebGLRenderer({ antialias: true });
  } catch (e) {
    return;
  }
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  container.appendChild(renderer.domElement);

  var scene = new THREE.Scene();
  scene.background = new THREE.Color(0x1d1f21);

  var camera = new THREE.PerspectiveCamera(50, 2, 0.1, 2000);

  scene.add(new THREE.HemisphereLight(0xcfe0f3, 0x2e3d4f, 0.4));
  var key = new THREE.DirectionalLight(0xffffff, 1.0);
  key.position.set(1, -1.5, 2);
  scene.add(key);
  var fill = new THREE.DirectionalLight(0xffffff, 0.25);
  fill.position.set(-1, 0.5, -1);
  scene.add(fill);

  var controls = new THREE.OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.08;

  var tumbler = new THREE.Group();
  scene.add(tumbler);
  var tumbling = true;
  controls.addEventListener('start', function () { tumbling = false; });
  controls.addEventListener('end', function () { tumbling = true; });

  function fit() {
    var w = container.clientWidth || 960;
    renderer.setSize(w, height);
    camera.aspect = w / height;
    camera.updateProjectionMatrix();
  }
  window.addEventListener('resize', fit);

  function animate() {
    requestAnimationFrame(animate);
    if (tumbling) {
      tumbler.rotation.x += 0.0035;
      tumbler.rotation.y += 0.008;
    }
    controls.update();
    renderer.render(scene, camera);
  }

  function makeAxes(n) {
    /* +axis bright, -axis dimmed, so direction is readable while tumbling */
    var pos = [], col = [];
    var add = function (x, y, z, hex) {
      var c = new THREE.Color(hex);
      pos.push(0, 0, 0, x * n, y * n, z * n);
      col.push(c.r, c.g, c.b, c.r, c.g, c.b);
    };
    add(1, 0, 0, 0xff4444); add(-1, 0, 0, 0x661d1d);
    add(0, 1, 0, 0x44ff44); add(0, -1, 0, 0x1d661d);
    add(0, 0, 1, 0x4d96ff); add(0, 0, -1, 0x1d3a66);
    var g = new THREE.BufferGeometry();
    g.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3));
    g.setAttribute('color', new THREE.Float32BufferAttribute(col, 3));
    return new THREE.LineSegments(g, new THREE.LineBasicMaterial({ vertexColors: true }));
  }

  new THREE.STLLoader().load(container.dataset.src || '/images/2019-04-13-oloid/oloid.stl', function (geometry) {
    geometry.computeBoundingBox();
    var size = geometry.boundingBox.getSize(new THREE.Vector3());
    var center = geometry.boundingBox.getCenter(new THREE.Vector3());
    geometry.translate(-center.x, -center.y, -center.z);
    var maxDim = Math.max(size.x, size.y, size.z) || 1;
    var mesh = new THREE.Mesh(geometry, new THREE.MeshPhongMaterial({
      color: 0x92b5d6,
      specular: 0x667788,
      shininess: 45,
      flatShading: true
    }));
    mesh.rotation.x = -Math.PI / 2; /* openscad z-up -> three y-up */
    tumbler.add(mesh);
    var axes = makeAxes(maxDim * 5);
    axes.rotation.x = -Math.PI / 2; /* match model z-up: blue z points up */
    tumbler.add(axes);
    camera.near = maxDim / 100;
    camera.far = maxDim * 100;
    camera.position.set(0.7, -1.1, 1.8).multiplyScalar(maxDim);
    controls.update();
  });

  fit();
  animate();
})();
