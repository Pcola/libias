#/bin/bash

VERSION=$1
APP_NAME=libias
BASE="/"$APP_NAME"/"
DIR=dist/prod
SRC=src/client

if [ -z "$VERSION" ]
then
  echo "VERSION was not provided."
  echo "usage: ./build-prod.sh <VERSION>"
  exit -1
fi

rm -rf dist/*

./release.sh $VERSION

# compile production package
cp ./config/env.config.prod.ts src/client/app/shared/config/env.config.ts
npm run build.prod  -- --base=$BASE

# copy resources to dist folder
cp -r ./node_modules/primeng/resources/themes/omega/* $DIR/css
cp -r ./node_modules/font-awesome/fonts $DIR
cp -r ./node_modules/font-awesome/css/* $DIR/css

# pack ZIP distribution package
rm -rf dist/$APP_NAME
mkdir dist/$APP_NAME
cp -r $DIR/* dist/$APP_NAME
rm -f dist/"$APP_NAME"*.zip
cd dist/libias
zip -r -q ../"$APP_NAME".war *
cd -
