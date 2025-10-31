#!/bin/bash

REVISION=$(date '+%Y.%m.%d')

echo

PS3='Publish Mode: '
PublishModeOptions=("Testing" "Stable")
PublishMode=
select optPublishMode in "${PublishModeOptions[@]}"
do
    case $optPublishMode in
        "Testing")
            PublishMode="testing"
            break
            ;;
        "Stable")
            PublishMode="stable"
            break
            ;;
        *) echo "invalid option $REPLY";;
    esac
done

echo

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

rm -f dist/netuno*.jar
rm -f dist/netuno*.zip
rm -f dist/netuno*.json

cd ..

echo
echo "MVN Package"
echo

cp netuno.cli/pom-base.xml netuno.cli/pom.xml

node bundle/publish-mode.js "$PublishMode"

./mvn-package.sh

node bundle/publish-mode.js

mkdir -p netuno.cli/out

mv netuno.cli/target/netuno-cli-*.jar netuno.cli/out/netuno.jar

mkdir -p netuno.cli/out/core/lib

rm -f netuno.cli/out/core/lib/*

cp netuno.cli/target/core/lib/* netuno.cli/out/core/lib/

cp netuno.cli/pom-setup.xml netuno.cli/pom.xml

node bundle/publish-mode.js "$PublishMode"

# build netuno-web.jar
mkdir -p netuno.tritao/out/temp
cd netuno.tritao/out
(cd temp; unzip -uo ../../../netuno.proteu/target/netuno-proteu-*.jar)
(cd temp; unzip -uo ../../../netuno.tritao/target/netuno-tritao-*.jar)
jar -cvf netuno-web-$REVISION.jar -C temp .
rm -rf temp
cd ../..

mvn --projects netuno.cli,netuno.psamata,netuno.library.doc -Drevision=$REVISION -Dmaven.test.skip=true clean package

node bundle/publish-mode.js

mv netuno.cli/target/netuno-setup.jar netuno.cli/out/netuno-setup.jar

cp netuno.cli/pom-base.xml netuno.cli/pom.xml

# executes bundle/index.js
cd bundle && node index.js $REVISION && cd ..

cd bundle/out && zip -q -r netuno.zip netuno/ && cd ../..

mkdir -p bundle/dist

cp netuno.cli/out/netuno-setup.jar bundle/dist/netuno-setup.jar

mv bundle/out/netuno.zip bundle/dist/netuno.zip

BuildVersion=`unzip -p bundle/out/netuno/netuno.jar META-INF/MANIFEST.MF | grep "Implementation-Build:" | grep -Eow "[0-9\.]+"`

cp bundle/dist/netuno.zip bundle/dist/netuno-`echo $BuildVersion | sed -E 's/[\\.]/_/g'`.zip

cd bundle
printf '{"version":"%s","type":"%s"}\n' "$BuildVersion" "$VersionType" > dist/netuno.json
cd ..

echo
echo
echo "Done."
echo

echo

PS3="Publish release $PublishMode: "
PublishReleaseOptions=("Yes" "No")
select optPublishRelease in "${PublishReleaseOptions[@]}"
do
    case $optPublishRelease in
        "Yes")
            node bundle/publish-release.js "$PublishMode"
            break
            ;;
        "No")
            echo
            break
            ;;
        *) echo "invalid option $REPLY";;
    esac
done

cd bundle
