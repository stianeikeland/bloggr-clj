# bløggr

Blog engine in clojure.

Based on: stasis, flexmark, enlive, optimus, ring. Syntax highlighting via the `pygmentize` binary.

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
