#!/usr/bin/env bash

set -euo pipefail

is_installed=false
if [ -f "./netuno.jar" ]; then
    is_installed=true
fi

versions=(
    'stable::graal-25.2.4/graalvm-community-jdk-25i2-25.0.4'
    'testing::graal-25.2.4/graalvm-community-jdk-25i2-25.0.4'
    '2026.06::jdk-25.0.2/graalvm-community-jdk-25.0.2'
    '2026.02::jdk-25.0.2/graalvm-community-jdk-25.0.2'
)

error() {
    echo -e "\033[0;31merror\033[0m:" "$@" >&2
    exit 1
}

warn() {
    echo -e "\033[0;33m$@ \033[0m"
}

info() {
    echo -e "\033[0;2m$@ \033[0m"
}

success() {
    echo -e "\033[0;32m$@ \033[0m"
}

graalVMVersionPath() {
    for entry in "${versions[@]}" ; do
        KEY="${entry%%::*}"
        VALUE="${entry##*::}"
        if [[ "$KEY" == $@ ]]; then
            echo "$VALUE"
            break
        fi
    done
}

echo
echo
echo -e "\033[0;37m                            .,;o'                      \033[0m"
echo -e "\033[0;37m                'o;,.   .,;oo~'                        \033[0m"
echo -e "\033[0;37m  N     N  eEEEee  TtttttT  u     u  N     N   oOOo    \033[0m"
echo -e "\033[0;37m  n n   N  E         |T|    u     u  n n   N  O    O   \033[0m"
echo -e "\033[0;37m  n  N  n  eEEE      !t!    U     U  n  N  n  o    o   \033[0m"
echo -e "\033[0;37m  N   n n  E         't'    U     U  N   n n  O    O   \033[0m"
echo -e "\033[0;37m  N     n  eEEEee     T      UuuuU   N     n   OooO    \033[0m"
echo -e "\033[0;36m                  ..,;ooddQOPttoc;,..                  \033[0m"
echo -e "\033[0;36m          .,;odlKWQ[~;'         '~;]QWKldo;,.          \033[0m"
echo -e "\033[0;36m      ,codloll=~'                     '~-+:={ldoc,     \033[0m"
echo -e "\033[0;36m   ,td&=}~'                                  '~;=%&t,  \033[0m"
echo
echo -e "\033[0;32mINSTALL SCRIPT\033[0m"
echo
if [ "$is_installed" = true ]; then
  echo -e "Installation will update the current folder."
else
  echo -e "Installation folder: \033[0;33m./netuno\033[0m"
fi
echo
info "Versions available:"

if [[ $# = 0 ]]; then
    netuno_versions=()
    i=0
    for entry in "${versions[@]}" ; do
        k="${entry%%::*}"
        netuno_versions[$i]=$k
        i=$(expr $i + 1)
    done
    PS3='Version to install: '
    select v in "${netuno_versions[@]}"
    do
        netuno_version="$v"
        if [ -n "$netuno_version" ]; then
          break
        fi
    done
else
    netuno_version=$1
fi

echo

# Select GraalVM version based on Netuno version
graalvm_version_path=$(graalVMVersionPath $netuno_version)

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
'Linux riscv64')
    error 'Not supported on riscv64'
    ;;
'Linux x86_64' | *)
    target=linux-x64
    ;;
esac

if [ "$is_installed" = false ]; then
    mkdir -p netuno
    cd netuno
fi

prefix="https://github.com/graalvm/graalvm-ce-builds/releases/download"
graalvm_url="${prefix}/${graalvm_version_path}_${target}_bin.tar.gz"
graalvm_version_part=${graalvm_version_path%%/*}
info "Downloading GraalVM: ${graalvm_version_part##*-}"
curl --fail --location --progress-bar --output graalvm.tar.gz $graalvm_url || { error 'Failed to download GraalVM'; }

if [ -d "core/graalvm" ]; then
    rm -rf core/graalvm
fi

mkdir -p core/graalvm

# Extract the GraalVM into de folder core/graalvm
info 'Extracting GraalVM...'
tar -xzf graalvm.tar.gz -C core/graalvm --strip-components=1 || { error 'Failed to extract GraalVM'; }

rm graalvm.tar.gz

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

if [ "$is_installed" = false ]; then
    echo
    echo "The Netuno commands above must be executed inside the folder path:"
    warn "$(pwd)"
    echo
    cd ..
fi

exit $?
