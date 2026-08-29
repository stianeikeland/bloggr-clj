#!/bin/bash
set -euo pipefail

bin/generate-thumbs
lein export

s3cmd sync -P dist/ s3://blog.eikeland.se \
      --no-mime-magic \
      --guess-mime-type \
      --cf-invalidate \
      --cf-invalidate-default-index \
      --add-header "Cache-Control: max-age=600"
