# bløggr [![Build Status](https://travis-ci.org/stianeikeland/bloggr-clj.svg?branch=master)](https://travis-ci.org/stianeikeland/bloggr-clj)

Blog engine in clojure.

Based on: stasis, flexmark, enlive, optimus, ring. Syntax highlighting via the `pygmentize` binary.

[ ![Codeship Status for stianeikeland/bloggr-clj](https://www.codeship.io/projects/7b4f0f20-f3c6-0131-703c-46df43419009/status?branch=master)](https://www.codeship.io/projects/27959)

## Usage

Requires `pygmentize` on PATH (`apt install python3-pygments`, `brew install pygments`, ...).

```
lein dev            # optional port: lein dev 3001
```

Export static site: `lein export` (writes to `dist/`).

Or from the REPL: `(require 'dev)` then `(dev/start)`, `(dev/stop)`, `(dev/restart)`.

## License

Copyright © 2014 Stian Eikeland

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
