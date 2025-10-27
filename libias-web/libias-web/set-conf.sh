#!/bin/bash

CONF_FILE=$1
JS_FILE=js/app.js

# check existence of config file
if [ ! -e $CONF_FILE ]
then
  echo "Configuration file is not available"
  echo "Use: ./set-conf.sh <conf file> <javascript file>"
  exit -1
fi

# check existence of target file
if [ ! -e $JS_FILE ]
then
  echo "Target file is not available"
  echo "Use: ./set-conf.sh <conf file> <javascript file>"
  exit -1
fi

# copy remote file to current dir
BASENAME=$(basename "$CONF_FILE")
EXT="${BASENAME##*.}"
FILENAME="${BASENAME%.*}"
FILENAME_EXT="$FILENAME"."$EXT"
cp -f $CONF_FILE .

# read config file lines into an array
index=0
while read line ; do
  if [[ "$line" =~ [^[:space:]] ]];
  then
    ENVARRAY[$index]="$line"
    index=$(($index+1))
  fi
done < $FILENAME_EXT

# replace all placeholders from config file to target file using 'sed' command
for e in "${ENVARRAY[@]}" ; do
  KEY="${e%%=*}"
  VALUE1="${e##*=}"
  VALUE=${VALUE1//\//\\/} # escape all slash characters / => \/
  if [ -n "$KEY" ] && [ -n "$VALUE" ];
  then
    echo "Replacing: $KEY => $VALUE1"
    sed -i "s/{$KEY}/$VALUE/g" $JS_FILE
  fi
done

echo "Successfully applied configuration..."
