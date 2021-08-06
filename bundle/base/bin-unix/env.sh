#!/usr/bin/env bash

DIR=`dirname "$0"`

JAVA_BIN=graalvm/bin/java

HOME=`dirname $DIR`

if [ "$HOME" = "$DIR" ]; then
        HOME=..
fi

PORT=`grep config.port $HOME/config.js | grep -E -o '[0-9]+'`

PID=`lsof -i tcp:${PORT} -s tcp:LISTEN | awk 'NR!=1 {print $2}'`

