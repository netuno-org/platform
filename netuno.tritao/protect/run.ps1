
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

New-Item -ItemType Directory -Force -Path temp

Move-Item -Force -Path out/artifacts/netuno-proteu.jar -Destination out/artifacts/netuno-proteu.zip
Move-Item -Force -Path out/artifacts/netuno-tritao.jar -Destination out/artifacts/netuno-tritao.zip

Expand-Archive -Force out/artifacts/netuno-proteu.zip -DestinationPath temp
Expand-Archive -Force out/artifacts/netuno-tritao.zip -DestinationPath temp

New-Item -ItemType Directory -Force -Path out/proguard

jar -cvf out/proguard/netuno-web.jar -C temp .

Remove-Item -Recurse -Force temp

#../../proguard/bin/proguard.bat @netuno-web-windows.pro