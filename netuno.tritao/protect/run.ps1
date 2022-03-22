
if(Test-Path -Path "out/artifacts/netuno-library-doc.jar"){
    rm -r "out/artifacts/netuno-library-doc.jar"
}


if(Test-Path -Path "out/artifacts/netuno-psamata.jar"){
    rm -r "out/artifacts/netuno-psamata.jar"
}

if(Test-Path -Path "out/artifacts/netuno-proteu.jar"){
    rm -r "out/artifacts/netuno-proteu.jar"
}

if(Test-Path -Path "out/artifacts/netuno-tritao.jar"){
    rm -r "out/artifacts/netuno-tritao.jar"
}

if(Test-Path -Path "out/proguard"){
    rm -r "out/proguard"
}



copy "../../netuno.library.doc/target/netuno-library-doc-*.jar" "out/artifacts/netuno-library-doc.jar"
copy "../../netuno.psamata/target/netuno-psamata-*.jar" "out/artifacts/netuno-psamata.jar"
copy "../../netuno.proteu/target/netuno-proteu-*.jar" "out/artifacts/netuno-proteu.jar"
copy "../target/netuno-tritao-*.jar" "out/artifacts/netuno-tritao.jar"

../../proguard/bin/proguard.bat @netuno-web-windows.pro