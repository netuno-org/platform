#!/bin/sh

REVISION=$(date '+%Y.%m.%d')

mvn -Drevision=$REVISION clean

mvn -Drevision=$REVISION compile

mvn -Dmaven.test.skip=true -Drevision=$REVISION package

