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

rm -f dist/netuno*.jar
rm -f dist/netuno*.zip
rm -f dist/netuno*.json

cd ..

echo
echo "MVN Install"
echo
./mvn-install.sh

echo
echo "MVN Package"
echo

cp netuno.cli/pom-base.xml netuno.cli/pom.xml

./mvn-package.sh

cd netuno.cli/protect && ./run.sh && cd ../..

mv netuno.cli/protect/out/proguard/netuno.jar netuno.cli/protect/out/proguard/netuno-base.jar

cd netuno.cli/protect/out/proguard

mkdir -p META-INF/services
echo "com.oracle.truffle.js.lang.JavaScriptLanguageProvider" > META-INF/services/com.oracle.truffle.api.provider.TruffleLanguageProvider
echo "com.oracle.graal.python.PythonLanguageProvider" >> META-INF/services/com.oracle.truffle.api.provider.TruffleLanguageProvider
echo "com.oracle.truffle.regex.RegexLanguageProvider" >> META-INF/services/com.oracle.truffle.api.provider.TruffleLanguageProvider
jar uf netuno-base.jar META-INF/services/com.oracle.truffle.api.provider.TruffleLanguageProvider
rm -rf META-INF
cd ../../../../

cp netuno.cli/pom-setup.xml netuno.cli/pom.xml

cd netuno.cli && mvn clean && mvn package && cd ..

cd netuno.cli/protect && ./run.sh && cd ../..

mv netuno.cli/protect/out/proguard/netuno.jar netuno.cli/protect/out/proguard/netuno-setup.jar

cp netuno.cli/pom-base.xml netuno.cli/pom.xml

mv netuno.cli/protect/out/proguard/netuno-base.jar netuno.cli/protect/out/proguard/netuno.jar

cd netuno.tritao/protect && ./run.sh && cd ../..

cd bundle && node index.js && cd ..

cd bundle/out && zip -r netuno.zip netuno/ && cd ../..

mkdir -p bundle/dist

cp netuno.cli/protect/out/proguard/netuno-setup.jar bundle/dist/netuno-setup.jar

mv bundle/out/netuno.zip bundle/dist/netuno.zip

BuildVersion=`unzip -p bundle/out/netuno/netuno.jar META-INF/MANIFEST.MF | grep "Build-Number:" | grep -Eow "[0-9\.]+"`

cp bundle/dist/netuno.zip bundle/dist/netuno-v7-`echo $BuildVersion | sed -E 's/[\\.]/_/g'`.zip

cd bundle
printf '{"version":"%s","type":"%s"}\n' "$BuildVersion" "$VersionType" > dist/netuno.json

echo
echo
echo "Done."
echo
