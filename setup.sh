#!/bin/bash

echo ""
echo "NETUNO - Workspace Setup"
echo ""

chmod +x *.sh
chmod +x bundle/*.sh
chmod +x netuno.cli/protect/run.sh
chmod +x netuno.tritao/protect/run.sh

###########################################
#
#  Proguard
#
###########################################

curl -L -o proguard.zip https://sourceforge.net/projects/proguard/files/latest/download

unzip proguard.zip 

mv `unzip -Z -1 proguard.zip | head -1 | rev | cut -c 2- | rev` proguard

rm -f proguard.zip

###########################################
#
#  Netuno Bundle - NPM Install
#
###########################################

cd bundle && npm install && cd ..

###########################################
#
# Netuno Bundle - Base Classes
#
###########################################

mkdir -p bundle/base/web/WEB-INF/classes/org/netuno

cd bundle/base/web/WEB-INF/classes/org/netuno && ln -s ../../../../../../../netuno.proteu/target/classes/org/netuno/proteu && cd ../../../../../../../

cd bundle/base/web/WEB-INF/classes/org/netuno && ln -s ../../../../../../../netuno.tritao/target/classes/org/netuno/tritao && cd ../../../../../../../

