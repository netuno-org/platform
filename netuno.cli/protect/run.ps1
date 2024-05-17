
if (Test-Path -Path "out/artifacts/netuno-cli.jar"){
    rm -r "out/artifacts/netuno-cli.jar"
}

copy "../target/netuno-cli.jar" "out/artifacts/netuno-cli.jar"

New-Item -ItemType Directory -Force -Path out/proguard

copy "../target/netuno-cli.jar" "out/proguard/netuno.jar"

#../../proguard/bin/proguard.bat @netuno-cli.pro

