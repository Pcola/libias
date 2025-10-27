#/bin/bash

if [ -z "$1" ]
then
  echo "VERSION was not provided."
  echo "usage: ./build-prod.sh <VERSION>"
  exit -1
fi

npm install
npm install -D @types/crypto-js
npm install -D @types/gulp-htmlmin

./build-prod.sh $1
