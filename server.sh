#!/usr/bin/env bash
set -e

./gradlew :example:assemble
cd example/build/web
python3 -m http.server 8080
