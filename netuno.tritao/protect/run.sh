#/bin/sh

cp -rf ../../netuno.library.doc/target/netuno-library-doc-*.jar out/artifacts/netuno-library-doc.jar
cp -rf ../../netuno.psamata/target/netuno-psamata-*.jar out/artifacts/netuno-psamata.jar
cp -rf ../../netuno.proteu/target/netuno-proteu-*.jar out/artifacts/netuno-proteu.jar
cp -rf ../target/netuno-tritao-*.jar out/artifacts/netuno-tritao.jar

PROGUARD_HOME=../../proguard

../../proguard/bin/proguard.sh @netuno-web.pro

