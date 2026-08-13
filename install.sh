#!/usr/bin/env bash

set -euo pipefail

color_off=''
red=''
dim='' # white

if [[ -t 1 ]]; then
    color_off='\033[0m' # text reset
    red='\033[0;31m'   # red
    green='\033[0;32m' # green
    dim='\033[0;2m'    # white
fi

error() {
    echo -e "${red}error${color_off}:" "$@" >&2
    cd ..
    rm -rf ./netuno
    exit 1
}

info() {
    echo -e "${dim}$@ ${color_off}"
}

success() {
    echo -e "${green}$@ ${color_off}"
}

graalVMVersion() {
    versions=(
        'stable::25.0.2'
        'testing::25.2.4'
        '2026.02::25.0.2'
        '2025.10::25.0.1'
    )
    for entry in "${versions[@]}" ; do
        KEY="${entry%%::*}"
        VALUE="${entry##*::}"
        if [[ "$KEY" == $@ ]]; then
            echo "$VALUE"
            break
        fi
    done
}

if [[ $# = 0 ]]; then
    PS3='Choose the Netuno version: '
    netuno_versions=('stable' 'testing' '2026.02' '2025.10')
    select version in "${netuno_versions[@]}"
    do
        netuno_version="$version"
        break
    done
else
    netuno_version=$1
fi

echo

# Select GraalVM version based on Netuno version
graalvm_version=$(graalVMVersion $netuno_version)

# Detect the Operating System and the CPU architecture
platform=$(uname -ms)

case $platform in
'Darwin x86_64')
    target=macos-x64
    ;;
'Darwin arm64')
    target=macos-aarch64
    ;;
'Linux aarch64' | 'Linux arm64')
    target=linux-aarch64
    ;;
'MINGW64'*)
    target=windows-x64
    ;;
'Linux riscv64')
    error 'Not supported on riscv64'
    ;;
'Linux x86_64' | *)
    target=linux-x64
    ;;
esac

mkdir -p netuno
cd netuno

prefix="https://github.com/graalvm/graalvm-ce-builds/releases/download"
if [[ $graalvm_version = '25.1.3' ]]; then
    graalvm_tar=graalvm-community-jdk-25i1-25.0.3_${target}_bin.tar.gz
    graalvm_url="${prefix}/graal-${graalvm_version}/${graalvm_tar}"
elif [[ $graalvm_version = '25.2.4' ]]; then
    graalvm_tar=graalvm-community-jdk-25i2-25.0.4_${target}_bin.tar.gz
    graalvm_url="${prefix}/graal-${graalvm_version}/${graalvm_tar}"
else
    graalvm_tar=graalvm-community-jdk-${graalvm_version}_${target}_bin.tar.gz 
    graalvm_url="${prefix}/jdk-${graalvm_version}/${graalvm_tar}"
fi

info "Downloading GraalVM version $graalvm_version"
curl --fail --location --progress-bar --output $graalvm_tar $graalvm_url || { error 'Failed to download GraalVM'; }

if [ -d "core/graalvm" ]; then
    rm -rf core/graalvm
fi

mkdir -p core/graalvm

# Extract the GraalVM into de folder core/graalvm
info 'Extracting GraalVM'
tar -xzf $graalvm_tar -C core/graalvm --strip-components=1 || { error 'Failed to extract GraalVM'; }

rm $graalvm_tar

if [[ $platform = Darwin* ]]; then
    mv core/graalvm/Contents/Home/* core/graalvm/
    rm -rf core/graalvm/Contents
fi

echo
info "Downloading Netuno Setup for the version: $netuno_version"
netuno_url="https://github.com/netuno-org/platform/releases/download/${netuno_version//./_}/netuno-setup.jar"
curl --fail --location --progress-bar --output netuno-setup.jar $netuno_url

./core/graalvm/bin/java --enable-native-access=ALL-UNNAMED -jar netuno-setup.jar install version=$netuno_version

# Ensure that Netuno works without permission errors on macOS
if [[ $platform = Darwin* ]]; then
    sudo xattr -r -d com.apple.quarantine .
fi

echo
echo "Netuno platform installed in the local folder:"
success "    ./netuno"
echo

exit $?
