#!/usr/bin/env bash
set -e

if [ ! -e example/build ]; then
	./gradlew build;
fi

cd example/build/web
python3 -m http.server 8080
