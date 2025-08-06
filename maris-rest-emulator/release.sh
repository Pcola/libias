#!/bin/bash

VERSION=$1
DATE=`date "+%d.%m.%Y %H:%M:%S %z"`

FILE=src/main/resources/release.properties
cat /dev/null > $FILE
echo "release.version=$VERSION" >> $FILE
echo "release.date=$DATE" >> $FILE


