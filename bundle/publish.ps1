
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

& ./mvn-package.ps1

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
Rename-Item -Path $webZip -NewName $webJar -Force

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

# Prepare bundle/dist
New-Item -ItemType Directory -Force -Path "bundle/dist" | Out-Null
Copy-Item "netuno.cli/out/netuno-setup.jar" "bundle/dist/netuno-setup.jar" -Force
Move-Item "bundle/out/netuno.zip" "bundle/dist/netuno.zip" -Force


# Extract BuildVersion from JAR manifest
$manifest = & ./unzip -p "bundle/out/netuno/netuno.jar" "META-INF/MANIFEST.MF"
$BuildVersion = ($manifest | Select-String "Implementation-Build:" | ForEach-Object { ($_ -split " ")[1] }).Trim()

