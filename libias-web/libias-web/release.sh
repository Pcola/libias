#!/bin/bash

VERSION=$1
DATE=`date "+%d.%m.%Y %H:%M:%S %z"`

FILE=src/client/release.json
cat /dev/null > $FILE
echo "{" >> $FILE
echo "\"version\":\"$VERSION\"," >> $FILE
echo "\"date\":\"$DATE\"" >> $FILE
echo "}" >> $FILE


