$PS3 = "Version Type: ";
$VersionTypeOptions = @('Upgrade', 'Critical')


$isCritical = Read-Host "This update is Crititcal [u] - Upgrade, [c] - Critical"
if ($isCritical -eq 'c') {
    $VersionType = "Critical" 
}else {
    $VersionType = "Upgrade"
}


rm -r dist

cd ..

Write-Host ""
Write-Host "MVN Install"
Write-Host ""
./mvn-install.ps1

Write-Host ""
Write-Host "MVN Package"
Write-Host ""
./mvn-package.ps1

cd ./netuno.cli/protect
./run.ps1
cd ../..

cd netuno.tritao/protect
./run.ps1
cd ../..

cd bundle
node index.js
cd ..

mkdir -p bundle/dist
copy "./bundle/out/netuno/netuno.jar" "./bundle/dist/netuno.jar"

Move-Item -Path "./bundle/out/netuno.zip" -Destination "./bundle/dist/netuno.zip"
cd bundle
node win.js type=$VersionType
cd ..