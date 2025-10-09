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

#curl -L -O https://github.com/Guardsquare/proguard/releases/download/v7.4.2/proguard-7.4.2.zip

#unzip proguard-7.4.2.zip 

#mv proguard-7.4.2 proguard
###mv `unzip -Z -1 proguard-7.4.2.zip | head -1 | rev | cut -c 2- | rev` proguard

#rm -rf proguard/examples

#rm -f proguard-7.4.2.zip

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

mkdir -p bundle/base/core/web/WEB-INF/classes/org/netuno

cd bundle/base/core/web/WEB-INF/classes/org/netuno && ln -s ../../../../../../../../netuno.proteu/target/classes/org/netuno/proteu && cd ../../../../../../../../

cd bundle/base/core/web/WEB-INF/classes/org/netuno && ln -s ../../../../../../../../netuno.tritao/target/classes/org/netuno/tritao && cd ../../../../../../../../

