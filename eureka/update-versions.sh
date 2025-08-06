#!/bin/sh

VER=$1

if [ -z "$VER" ];
then
  echo "VERSION was not provided."
  echo "usage: ./update-version.sh <VERSION>"
  exit -1
fi

mvn versions:set -DnewVersion=$VER
