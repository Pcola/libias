#!/bin/bash

VERSION=$1

if [ -z $1 ]; then
  echo "release version is not set..."
  echo "usage: ./build.sh <version>"
  exit -1;
fi

./release.sh $VERSION
./update-versions.sh $VERSION

mvn -DskipTests clean package
