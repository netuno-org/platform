#!/bin/bash

PS3='Version Type: '
VersionTypeOptions=("Upgrade" "Critical")
VersionType=
select optVersionType in "${VersionTypeOptions[@]}"
do
    case $optVersionType in
        "Upgrade")
            VersionType="upgrade"
            break
            ;;
        "Critical")
            VersionType="critical"
            break
            ;;
        *) echo "invalid option $REPLY";;
    esac
done

rm -f netuno-v7-*.zip

cd ..

mvn clean
mvn compile
mvn -Dmaven.test.skip=true package

cd netuno.cli/protect && ./run.sh && cd ../..

cd netuno.tritao/protect && ./run.sh && cd ../..

cd bundle && node index.js && cd ..

mkdir bundle/dist

cp bundle/out/netuno/netuno.jar bundle/dist/netuno.jar

mv bundle/out/netuno.zip bundle/dist/netuno.zip

cp bundle/netuno.zip bundle/dist/netuno-v7-latest.zip

cp bundle/out/netuno/netuno.jar bundle/dist/netuno-latest.jar

BuildVersion=`unzip -p netuno.cli/target/netuno-cli-*-jar-with-dependencies.jar META-INF/MANIFEST.MF | grep "Build-Number:" | grep -Eow "[0-9\.]+"`

cp bundle/dist/netuno.zip bundle/dist/netuno-v7-`echo $BuildVersion | sed -E 's/[\\.]/_/g'`.zip

cd bundle

printf '{"version":"%s","type":"%s"}\n' "$BuildVersion" "$VersionType" > dist/netuno.json


