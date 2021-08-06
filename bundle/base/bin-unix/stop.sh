#!/usr/bin/env bash

DIR=`dirname "$0"`

source $DIR/env.sh

$DIR/show.sh

if [ ! -z "$PID" ]
then
        kill $PID
        echo "Kill signals to $PID"
else
        echo "Process not found because is not running."
fi

echo ""

