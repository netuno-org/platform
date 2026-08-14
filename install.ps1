$OutputEncoding = [System.Text.Encoding]::UTF8

$color_off = ""
$red = ""
$green = ""
$dim = ""

if ($Host.UI.RawUI -and $psCmdlet.SessionState.QueryForInterface) {
    $color_off = "`e[0m"
    $red = "`e[0;31m"
    $green = "`e[0;32m"
    $dim = "`e[0;2m"
}

function Write-ErrorCustom {
    param([string]$Message)
    Write-Host "${red}error${color_off}: $Message" -ForegroundColor Red
    Set-Location ..
    if (Test-Path ./netuno) {
        Remove-Item -Recurse -Force ./netuno
    }
    Exit 1
}

function Write-InfoCustom {
    param([string]$Message)
    Write-Host "${dim}$Message ${color_off}"
}

function Write-SuccessCustom {
    param([string]$Message)
    Write-Host "${green}$Message ${color_off}"
}

function Get-GraalVMVersion {
    param([string]$NetunoVersion)
    
    $versions = @{
        'stable'  = '25.0.2'
        'testing' = '25.2.4'
        '2026.02' = '25.0.2'
        '2025.10' = '25.0.1'
    }
    
    if ($versions.ContainsKey($NetunoVersion)) {
        return $versions[$NetunoVersion]
    }
}

# Seleção da versão do Netuno
$netuno_version = ""
if ($args.Count -eq 0) {
    $netuno_versions = @('stable', 'testing', '2026.02', '2025.10')
    
    Write-Host "Choose the Netuno version:"
    for ($i = 0; $i -lt $netuno_versions.Count; $i++) {
        Write-Host "$($i + 1)) $($netuno_versions[$i])"
    }
    
    do {
        $selection = Read-Host "Choose the Netuno version"
        if ($selection -match '^\d+$' -and [int]$selection -ge 1 -and [int]$selection -le $netuno_versions.Count) {
            $netuno_version = $netuno_versions[[int]$selection - 1]
        }
    } while (-not $netuno_version)
}
else {
    $netuno_version = $args[0]
}

Write-Host ""

# Seleciona a versão do GraalVM
$graalvm_version = Get-GraalVMVersion $netuno_version
$target = "windows-x64"

# Criação do diretório e navegação
New-Item -ItemType Directory -Force -Path netuno | Out-Null
Set-Location netuno

$prefix = "https://github.com/graalvm/graalvm-ce-builds/releases/download"
$ext = "zip"

if ($graalvm_version -eq '25.0.1') {
    $graalvm_archive = "graalvm-community-jdk-25.0.1_${target}_bin.${ext}"
    $graalvm_url = "${prefix}/jdk-25.0.1/${graalvm_archive}"
}
elseif ($graalvm_version -eq '25.0.2') {
    $graalvm_archive = "graalvm-community-jdk-25.0.2_${target}_bin.${ext}"
    $graalvm_url = "${prefix}/jdk-25.0.2/${graalvm_archive}"
}
elseif ($graalvm_version -eq '25.1.3') {
    $graalvm_archive = "graalvm-community-jdk-25.1.3_${target}_bin.${ext}"
    $graalvm_url = "${prefix}/jdk-25.1.3/${graalvm_archive}"
}
elseif ($graalvm_version -eq '25.2.4') {
    $graalvm_archive = "graalvm-community-jdk-25.2.4_${target}_bin.${ext}"
    $graalvm_url = "${prefix}/jdk-25.2.4/${graalvm_archive}"
}
else {
    $graalvm_archive = "graalvm-community-jdk-${graalvm_version}_${target}_bin.${ext}" 
    $graalvm_url = "${prefix}/graal-${graalvm_version}/${graalvm_archive}"
}

Write-InfoCustom "Downloading GraalVM version $graalvm_version"

try {
    $OldProgressPreference = $ProgressPreference
    $ProgressPreference = 'SilentlyContinue'

    Invoke-WebRequest -Uri $graalvm_url -OutFile $graalvm_archive -UserAgent "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" -UseBasicParsing
    
    $ProgressPreference = $OldProgressPreference
}
catch {
    Write-ErrorCustom "Failed to download GraalVM from $graalvm_url"
}


if (Test-Path "core/graalvm") {
    Remove-Item -Recurse -Force "core/graalvm"
}

New-Item -ItemType Directory -Force -Path "core/graalvm" | Out-Null

# Extrair o GraalVM para a pasta core/graalvm
Write-InfoCustom 'Extracting GraalVM'

$archive_to_extract = if (Get-Variable -Name "graalvm_archive" -ErrorAction SilentlyContinue) { $graalvm_archive } else { $graalvm_tar }

try {
    $temp_extract = "core/graalvm_temp"
    if (Test-Path $temp_extract) { Remove-Item -Recurse -Force $temp_extract }
        
    Expand-Archive -Path $archive_to_extract -DestinationPath $temp_extract -Force
        
    # Simula o --strip-components=1: entra na pasta gerada e move o conteúdo direto para core/graalvm
    $inner_folder = Get-ChildItem -Path $temp_extract -Directory | Select-Object -First 1
    Move-Item -Path "$($inner_folder.FullName)\*" -Destination "core/graalvm" -Force
    Remove-Item -Recurse -Force $temp_extract
}
catch {
    Write-ErrorCustom "Failed to extract GraalVM: $_"
}

if ($archive_to_extract -and (Test-Path $archive_to_extract)) {
    Remove-Item -Force $archive_to_extract
}

# Download Netuno
Write-Host ""
Write-InfoCustom "Downloading Netuno Setup for the version: $netuno_version"

$netuno_version_under = $netuno_version -replace '\.', '_'
$netuno_url = "https://github.com/netuno-org/platform/releases/download/${netuno_version_under}/netuno-setup.jar"

try {
    Invoke-WebRequest -Uri $netuno_url -OutFile "netuno-setup.jar" -UserAgent "Mozilla/5.0"
}
catch {
    Write-ErrorCustom 'Failed to download Netuno Setup'
}

# Executa o instalador Java do GraalVM que foi feito download
Write-InfoCustom "Running Netuno Setup..."

$run_dir = "core/graalvm_run"
if (Test-Path $run_dir) { Remove-Item -Recurse -Force $run_dir }
    
# Copia o GraalVM para uma pasta temporária de execução
New-Item -ItemType Directory -Force -Path $run_dir | Out-Null
Copy-Item -Path "core/graalvm/*" -Destination $run_dir -Recurse -Force
    
# Executa a partir da pasta temporária para libertar a pasta oficial 'core/graalvm'
& "$run_dir/bin/java" --enable-native-access=ALL-UNNAMED -jar netuno-setup.jar install version=$netuno_version
    
# Limpa a pasta temporária após a instalação concluir
if (Test-Path $run_dir) { Remove-Item -Recurse -Force $run_dir }

Write-Host ""
Write-Host "Netuno platform installed in the local folder:"
Write-SuccessCustom "    ./netuno"
Write-Host ""

Set-Location ..

if (Test-Path "./install.ps1") {
    Remove-Item -Force "./install.ps1"
}
