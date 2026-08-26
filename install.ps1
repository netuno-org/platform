$ErrorActionPreference = 'Stop'

$script:original_location = Get-Location

$is_installed = Test-Path './netuno.jar' -PathType Leaf

$versions = [ordered]@{
    'stable'  = 'jdk-25.0.2/graalvm-community-jdk-25.0.2'
    'testing' = 'graal-25.2.4/graalvm-community-jdk-25i2-25.0.4'
    '2026.06' = 'jdk-25.0.2/graalvm-community-jdk-25.0.2'
    '2026.02' = 'jdk-25.0.2/graalvm-community-jdk-25.0.2'
}

function Write-ErrorCustom {
    param([string]$Message)

    Write-Host 'error' -ForegroundColor DarkRed -NoNewline
    Write-Host ": $Message"

    Set-Location $script:original_location

    exit 1
}

function Write-WarnCustom {
    param([string]$Message)

    Write-Host "$Message " -ForegroundColor DarkYellow
}

function Write-InfoCustom {
    param([string]$Message)

    Write-Host "$Message " -ForegroundColor DarkGray
}

function Write-SuccessCustom {
    param([string]$Message)

    Write-Host "$Message " -ForegroundColor DarkGreen
}

function Get-GraalVMVersionPath {
    param([string]$NetunoVersion)

    foreach ($key in $script:versions.Keys) {
        if ($key -eq $NetunoVersion) {
            return $script:versions[$key]
        }
    }

    return ''
}

trap {
    Write-ErrorCustom $_.Exception.Message
}

$banner_top = @'
                            .,;o'                      
                'o;,.   .,;oo~'                        
  N     N  eEEEee  TtttttT  u     u  N     N   oOOo    
  n n   N  E         |T|    u     u  n n   N  O    O   
  n  N  n  eEEE      !t!    U     U  n  N  n  o    o   
  N   n n  E         't'    U     U  N   n n  O    O   
  N     n  eEEEee     T      UuuuU   N     n   OooO    
'@

$banner_bottom = @'
                  ..,;ooddQOPttoc;,..                  
          .,;odlKWQ[~;'         '~;]QWKldo;,.          
      ,codloll=~'                     '~-+:={ldoc,     
   ,td&=}~'                                  '~;=%&t,  
'@

Write-Host ''
Write-Host ''
Write-Host $banner_top -ForegroundColor Gray
Write-Host $banner_bottom -ForegroundColor DarkCyan
Write-Host ''
Write-SuccessCustom 'INSTALL SCRIPT'
Write-Host ''

if ($is_installed) {
    Write-Host 'Installation will update the current folder.'
}
else {
    Write-Host 'Installation folder: ' -NoNewline
    Write-Host './netuno' -ForegroundColor DarkYellow
}

Write-Host ''
Write-InfoCustom 'Versions available:'

$netuno_version = ''

if ($args.Count -eq 0) {
    $netuno_versions = @($versions.Keys)

    while (-not $netuno_version) {
        for ($i = 0; $i -lt $netuno_versions.Count; $i++) {
            Write-Host "$($i + 1)) $($netuno_versions[$i])"
        }

        do {
            $selection = Read-Host 'Version to install'

            if ($selection -match '^\d+$' -and [int]$selection -ge 1 -and [int]$selection -le $netuno_versions.Count) {
                $netuno_version = $netuno_versions[[int]$selection - 1]
            }
        } while (-not $netuno_version -and $selection)
    }
}
else {
    $netuno_version = $args[0]
}

Write-Host ''

$graalvm_version_path = Get-GraalVMVersionPath $netuno_version

$target = 'windows-x64'

if (-not $is_installed) {
    New-Item -ItemType Directory -Force -Path 'netuno' | Out-Null
    Set-Location 'netuno'
}

$prefix = 'https://github.com/graalvm/graalvm-ce-builds/releases/download'
$graalvm_url = "$prefix/${graalvm_version_path}_${target}_bin.zip"
$graalvm_version_part = $graalvm_version_path.Split('/')[0]
$graalvm_version = $graalvm_version_part.Substring($graalvm_version_part.LastIndexOf('-') + 1)

Write-InfoCustom "Downloading GraalVM: $graalvm_version"

curl.exe --fail --location --progress-bar --output 'graalvm.zip' $graalvm_url

if ($LASTEXITCODE -ne 0) {
    Write-ErrorCustom 'Failed to download GraalVM'
}

if (Test-Path 'core/graalvm' -PathType Container) {
    Remove-Item -Recurse -Force 'core/graalvm'
}

New-Item -ItemType Directory -Force -Path 'core/graalvm' | Out-Null

Write-InfoCustom 'Extracting GraalVM...'

tar.exe -xf 'graalvm.zip' -C 'core/graalvm' --strip-components=1

if ($LASTEXITCODE -ne 0) {
    Write-ErrorCustom 'Failed to extract GraalVM'
}

Remove-Item -Force 'graalvm.zip'

Write-Host ''
Write-InfoCustom "Downloading Netuno Setup for the version: $netuno_version"

$netuno_version_tag = $netuno_version -replace '\.', '_'
$netuno_url = "https://github.com/netuno-org/platform/releases/download/$netuno_version_tag/netuno-setup.jar"

curl.exe --fail --location --progress-bar --output 'netuno-setup.jar' $netuno_url

if ($LASTEXITCODE -ne 0) {
    Write-ErrorCustom 'Failed to download Netuno Setup'
}

& './core/graalvm/bin/java.exe' --enable-native-access=ALL-UNNAMED -jar netuno-setup.jar install version=$netuno_version

if ($LASTEXITCODE -ne 0) {
    Write-ErrorCustom "Netuno Setup failed with exit code $LASTEXITCODE"
}

if (-not $is_installed) {
    Write-Host ''
    Write-Host 'The Netuno commands above must be executed inside the folder path:'
    Write-WarnCustom (Get-Location).Path
    Write-Host ''
}

Set-Location $script:original_location

exit 0
