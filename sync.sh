#!/bin/bash
s3cmd sync -P dist/ s3://blog.eikeland.se --cf-invalidate --cf-invalidate-default-index --add-header "Cache-Control: max-age=600"
