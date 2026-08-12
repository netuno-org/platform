#!/bin/bash

color_off=''
red=''
dim='' # white

if [[ -t 1 ]]; then
    color_off='\033[0m' # text reset
    red='\033[0;31m'   # red
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

declare -A graalvm_versions 
graalvm_versions['stable']='25.0.2'
graalvm_versions['testing']='25.2.4'
graalvm_versions['2026.02']='25.0.2'
graalvm_versions['2025.10']='25.0.1'
# graalvm_versions['2025.08']='24.0.2'
# graalvm_versions['2025.04']='24.0.0'
# graalvm_versions['2025.03']='23.0.2'
 
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

# Select GraalVM version based on Netuno version
graalvm_version=${graalvm_versions["$netuno_version"]}

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
curl --fail --location --progress-bar --output=$graalvm_tar $graalvm_url || { error 'Failed to download GraalVM'; }

mkdir -p core/graalvm

info 'Extracting GraalVM'
tar --extract --ungzip --checkpoint=1000 --checkpoint-action=dot --file=$graalvm_tar --directory=core/graalvm --strip-components=1 || { error 'Failed to extract GraalVM'; }

rm $graalvm_tar

echo
info "Downloading Netuno version $netuno_version"
netuno_url="https://github.com/netuno-org/platform/releases/download/${netuno_version//./_}/netuno-setup.jar"
curl --fail --location --progress-bar --output=netuno-setup.jar $netuno_url

./core/graalvm/bin/java -jar netuno-setup.jar install version=$netuno_version

# Ensure that Netuno works without permission errors on macOS
if [[ $platform = Darwin* ]]; then
    sudo xattr -r -d com.apple.quarantine .
fi
