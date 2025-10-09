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

