# ================================
# --- Clean caches before run ---
# ================================

Write-Host "Cleaning caches..." -ForegroundColor Yellow

# Netuno build leftovers
$buildDirs = @(
    "netuno.cli/target",
    "netuno.cli/out",
    "netuno.tritao/target",
    "netuno.proteu/target",
    "bundle/out",
    "bundle/dist",
    "dist"
)

foreach ($dir in $buildDirs) {
    if (Test-Path $dir) {
        Write-Host " - Cleaning $dir"
        Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $dir
    }
}

Write-Host "Cache cleanup done." -ForegroundColor Green
Write-Host ""

# --- Revision Date ---
$REVISION = Get-Date -Format "yyyy.MM.dd"
Write-Host ""

# --- Publish Mode Selection ---
$PublishModeOptions = @("Testing", "Stable")
Write-Host "Publish Mode:"
for ($i = 0; $i -lt $PublishModeOptions.Count; $i++) {
    Write-Host "$($i+1)) $($PublishModeOptions[$i])"
}
do {
    $choice = Read-Host "Select an option (1-$($PublishModeOptions.Count))"
} while (-not ($choice -match '^[1-2]$'))
$PublishMode = if ($choice -eq 1) { "testing" } else { "stable" }
Write-Host ""

# --- Version Type selection ---
$VersionTypeOptions = @("Upgrade", "Critical")
Write-Host "Version Type:"
for ($i = 0; $i -lt $VersionTypeOptions.Count; $i++) {
    Write-Host "$($i+1)) $($VersionTypeOptions[$i])"
}
do {
    $choice = Read-Host "Select an option (1-$($VersionTypeOptions.Count))"
} while (-not ($choice -match '^[1-2]$'))
$VersionType = if ($choice -eq 1) { "upgrade" } else { "critical" }

# --- Clean Previous build ---
Remove-Item -Force -ErrorAction SilentlyContinue "./dist/netuno*.jar"
Remove-Item -Force -ErrorAction SilentlyContinue "./dist/netuno*.zip"
Remove-Item -Force -ErrorAction SilentlyContinue "./dist/netuno*.json"

Set-Location ".."

Write-Host ""
Write-Host "MVN Package"
Write-Host ""

Copy-Item "netuno.cli/pom-base.xml" "netuno.cli/pom.xml" -Force

& node "bundle/publish-mode.js" $PublishMode

& ./build.ps1

& node "bundle/publish-mode.js"

# --- Build netuno cli ---
New-Item -ItemType Directory -Force -Path "netuno.cli/out" | Out-Null
Move-Item "netuno.cli/target/netuno-cli-*.jar" "netuno.cli/out/netuno.jar" -Force

New-Item -ItemType Directory -Force -Path "netuno.cli/out/core/lib" | Out-Null
Remove-Item -Force -ErrorAction SilentlyContinue "netuno.cli/out/core/lib/*"

Copy-Item "netuno.cli/target/core/lib/*" "netuno.cli/out/core/lib/" -Recurse -Force

Copy-Item "netuno.cli/pom-setup.xml" "netuno.cli/pom.xml" -Force
& node "bundle/publish-mode.js" $PublishMode

# --- build netuno web ---
New-Item -ItemType Directory -Force -Path "netuno.tritao/out/temp" | Out-Null
Set-Location "netuno.tritao/out"

# --- Expand proteu jar ---
$proteuJar = Get-ChildItem "./../../netuno.proteu/target/netuno-proteu-*.jar" -File -ErrorAction SilentlyContinue
if (-not $proteuJar) { throw "Proteu JAR not found" }

$proteuZip = [System.IO.Path]::ChangeExtension($proteuJar.FullName, ".zip")
Rename-Item $proteuJar.FullName $proteuZip -Force
Expand-Archive -Path $proteuZip -DestinationPath "temp" -Force

# --- Expand tritao jar ---
$tritaoJar = Get-ChildItem "./../../netuno.tritao/target/netuno-tritao-*.jar" -File -ErrorAction SilentlyContinue
if (-not $tritaoJar) { throw "Tritao JAR not found" }

$tritaoZip = [System.IO.Path]::ChangeExtension($tritaoJar.FullName, ".zip")
Rename-Item $tritaoJar.FullName $tritaoZip -Force
Expand-Archive -Path $tritaoZip -DestinationPath "temp" -Force

# --- Repack ---
$webZip = "netuno-web-$REVISION.zip"
$webJar = "netuno-web-$REVISION.jar"

Compress-Archive -Path "temp/*" -DestinationPath $webZip -Force

if (Test-Path $webJar) {
    Remove-Item -Path $webJar -Force
}
Rename-Item -Path $webZip -NewName $webJar

# --- Cleanup ---
Remove-Item -Recurse -Force "temp"

# --- Restore original filenames ---
Rename-Item $proteuZip $proteuJar.FullName -Force
Rename-Item $tritaoZip $tritaoJar.FullName -Force

Set-Location "../.."

& mvn --projects netuno.cli,netuno.psamata,netuno.library.doc -Drevision="$REVISION" -DskipTests=true clean package
& node "./bundle/publish-mode.js"

Move-Item "netuno.cli/target/netuno-setup.jar" "netuno.cli/out/netuno-setup.jar" -Force
Copy-Item "netuno.cli/pom-base.xml" "netuno.cli/pom.xml" -Force

Set-Location "bundle"
& node "index.js" $REVISION
Set-Location ".."
$BuildVersion

# compress
Set-Location "bundle/out"
if (Test-Path "netuno.zip") { Remove-Item "netuno.zip" -Force }
& "7z" a -tzip "netuno.zip" "netuno\*" -mx=1
Set-Location "../.."


# Prepare bundle/dist
New-Item -ItemType Directory -Force -Path "bundle/dist" | Out-Null
Copy-Item "netuno.cli/out/netuno-setup.jar" "bundle/dist/netuno-setup.jar" -Force
Move-Item "bundle/out/netuno.zip" "bundle/dist/netuno.zip" -Force

# Extract the MANIFEST.MF content using 7z
$manifest = & 7z e -so "bundle/out/netuno/netuno.jar" "META-INF/MANIFEST.MF"
$BuildVersion = ($manifest | Select-String "Implementation-Build:" | ForEach-Object { ($_ -split " ")[1] }).Trim()

# Create versioned zip copy
Copy-Item "bundle/dist/netuno.zip" ("bundle/dist/netuno-" + ($BuildVersion -replace '\.', '_') + ".zip") -Force

# Write JSON metadata
Set-Location "bundle"
$metadata = @{
    version = $BuildVersion
    type    = $VersionType
} | ConvertTo-Json
$metadata | Out-File -Encoding UTF8 "dist/netuno.json"
Set-Location ".."

Write-Host ""
Write-Host "Done."
Write-Host ""

# --- Optional release publishing ---
Write-Host "Publish release " + $PublishMode + ":"
$PublishReleaseOptions = @("Yes", "No")
for ($i = 0; $i -lt $PublishReleaseOptions.Count; $i++) {
    Write-Host "$($i+1)) $($PublishReleaseOptions[$i])"
}
do {
    $choice = Read-Host "Select an option (1-$($PublishReleaseOptions.Count))"
} while (-not ($choice -match '^[1-2]$'))

if ($choice -eq 1) {
    & node "bundle/publish-release.js" $PublishMode
} else {
    Write-Host ""
}

Set-Location "bundle"