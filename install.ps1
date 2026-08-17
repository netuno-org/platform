$OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$script:original_location = Get-Location
$script:netuno_created = $false

$graalvm_versions_by_netuno = [ordered]@{
    'stable'  = '25.0.2'
    'testing' = '25.2.4'
    '2026.02' = '25.0.2'
    '2025.10' = '25.0.1'
}

function Write-ErrorCustom {
    param([string]$Message)

    Write-Host "error: $Message" -ForegroundColor Red

    Set-Location $script:original_location

    if ($script:netuno_created -and (Test-Path ./netuno)) {
        Remove-Item -Recurse -Force ./netuno -ErrorAction SilentlyContinue
    }

    Exit 1
}

function Write-InfoCustom {
    param([string]$Message)

    Write-Host $Message -ForegroundColor DarkGray
}

function Write-SuccessCustom {
    param([string]$Message)

    Write-Host $Message -ForegroundColor Green
}

function Get-GraalVMVersion {
    param([string]$NetunoVersion)

    if (-not $graalvm_versions_by_netuno.Contains($NetunoVersion)) {
        Write-ErrorCustom "Unknown Netuno version: $NetunoVersion"
    }

    return $graalvm_versions_by_netuno[$NetunoVersion]
}

function Get-GraalVMDownload {
    param(
        [string]$GraalVMVersion,
        [string]$Target
    )

    $parts = $GraalVMVersion.Split('.')

    if ($parts.Count -ne 3) {
        Write-ErrorCustom "Invalid GraalVM version: $GraalVMVersion"
    }

    $major = $parts[0]
    $minor = [int]$parts[1]
    $security = $parts[2]

    if ($minor -eq 0) {
        $tag = "jdk-$GraalVMVersion"
        $archive = "graalvm-community-jdk-${GraalVMVersion}_${Target}_bin.zip"
    }
    else {
        $tag = "graal-$GraalVMVersion"
        $archive = "graalvm-community-jdk-${major}i${minor}-${major}.0.${security}_${Target}_bin.zip"
    }

    return [pscustomobject]@{
        Archive = $archive
        Url     = "https://github.com/graalvm/graalvm-ce-builds/releases/download/$tag/$archive"
    }
}

$netuno_version = ""

if ($args.Count -eq 0) {
    $available_versions = @($graalvm_versions_by_netuno.Keys)

    Write-Host "Choose the Netuno version:"

    for ($i = 0; $i -lt $available_versions.Count; $i++) {
        Write-Host "$($i + 1)) $($available_versions[$i])"
    }

    do {
        $selection = Read-Host "Number"

        if ($selection -match '^\d+$' -and [int]$selection -ge 1 -and [int]$selection -le $available_versions.Count) {
            $netuno_version = $available_versions[[int]$selection - 1]
        }
    } while (-not $netuno_version)
}
else {
    $netuno_version = $args[0]
}

Write-Host ""

$target = "windows-x64"
$graalvm_version = Get-GraalVMVersion $netuno_version
$graalvm_download = Get-GraalVMDownload $graalvm_version $target

if (-not (Test-Path ./netuno)) {
    $script:netuno_created = $true
}

New-Item -ItemType Directory -Force -Path netuno | Out-Null
Set-Location netuno

Write-InfoCustom "Downloading GraalVM version $graalvm_version"

$old_progress_preference = $ProgressPreference
$ProgressPreference = 'SilentlyContinue'

try {
    Invoke-WebRequest -Uri $graalvm_download.Url -OutFile $graalvm_download.Archive -UserAgent "Mozilla/5.0" -UseBasicParsing
}
catch {
    Write-ErrorCustom "Failed to download GraalVM from $($graalvm_download.Url)"
}
finally {
    $ProgressPreference = $old_progress_preference
}

$graalvm_path = "core/graalvm"

if (Test-Path $graalvm_path) {
    Remove-Item -Recurse -Force $graalvm_path
}

New-Item -ItemType Directory -Force -Path $graalvm_path | Out-Null

Write-InfoCustom "Extracting GraalVM"

$extracted = $false

if (Get-Command tar -ErrorAction SilentlyContinue) {
    tar -xf $graalvm_download.Archive -C $graalvm_path --strip-components=1
    $extracted = $LASTEXITCODE -eq 0
}

if (-not $extracted) {
    try {
        $temp_extract = "core/graalvm_temp"

        if (Test-Path $temp_extract) {
            Remove-Item -Recurse -Force $temp_extract
        }

        Expand-Archive -Path $graalvm_download.Archive -DestinationPath $temp_extract -Force

        $inner_folder = Get-ChildItem -Path $temp_extract -Directory | Select-Object -First 1
        Move-Item -Path "$($inner_folder.FullName)\*" -Destination $graalvm_path -Force

        Remove-Item -Recurse -Force $temp_extract
    }
    catch {
        Write-ErrorCustom "Failed to extract GraalVM: $_"
    }
}

if (-not (Test-Path "$graalvm_path/bin/java.exe")) {
    Write-ErrorCustom "GraalVM extraction did not produce a valid JDK layout"
}

if (Test-Path $graalvm_download.Archive) {
    Remove-Item -Force $graalvm_download.Archive
}

Write-Host ""
Write-InfoCustom "Downloading Netuno Setup for the version: $netuno_version"

$netuno_version_tag = $netuno_version -replace '\.', '_'
$netuno_url = "https://github.com/netuno-org/platform/releases/download/$netuno_version_tag/netuno-setup.jar"

try {
    Invoke-WebRequest -Uri $netuno_url -OutFile "netuno-setup.jar" -UserAgent "Mozilla/5.0" -UseBasicParsing
}
catch {
    Write-ErrorCustom "Failed to download Netuno Setup from $netuno_url"
}

Write-InfoCustom "Running Netuno Setup..."

$run_path = "core/graalvm_run"

if (Test-Path $run_path) {
    Remove-Item -Recurse -Force $run_path
}

New-Item -ItemType Directory -Force -Path $run_path | Out-Null
Copy-Item -Path "$graalvm_path/*" -Destination $run_path -Recurse -Force

& "$run_path/bin/java" --enable-native-access=ALL-UNNAMED -jar netuno-setup.jar install version=$netuno_version
$setup_exit_code = $LASTEXITCODE

Remove-Item -Recurse -Force $run_path -ErrorAction SilentlyContinue

if ($setup_exit_code -ne 0) {
    Write-ErrorCustom "Netuno Setup failed with exit code $setup_exit_code"
}

Set-Location $script:original_location

Write-Host ""
Write-Host "Netuno platform installed in the local folder:"
Write-SuccessCustom "    ./netuno"
Write-Host ""