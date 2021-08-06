#!/usr/bin/env bash

DIR=`dirname "$0"`

source $DIR/env.sh

$DIR/show.sh

if [ -z "$PID" ]
then
        cd $HOME

        ./netuno server > server.out &

        disown -h
fi

echo ""

