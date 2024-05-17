$PS3 = "Version Type: ";
$VersionTypeOptions = @('Upgrade', 'Critical')


$isCritical = Read-Host "This update is Crititcal [u] - Upgrade, [c] - Critical"
if ($isCritical -eq 'c') {
    $VersionType = "Critical" 
}else {
    $VersionType = "Upgrade"
}


Remove-Item "dist" -Force -ErrorAction Ignore 


cd ..

Write-Host ""
Write-Host "MVN Install"
Write-Host ""
./mvn-install.ps1

Write-Host ""
Write-Host "MVN Package"
Write-Host ""
copy netuno.cli/pom-base.xml netuno.cli/pom.xml
./mvn-package.ps1

cd ./netuno.cli/protect

if (Test-Path -Path "out/proguard") {
    rm -r "out/proguard"
}

./run.ps1
cd ../..

Move-Item -Path "./netuno.cli/protect/out/proguard/netuno.jar" -Destination "./netuno.cli/protect/out/proguard/netuno-base.jar"

copy "./netuno.cli/pom-setup.xml" "./netuno.cli/pom.xml"

cd netuno.cli
mvn clean
mvn package
cd ..

cd ./netuno.cli/protect
./run.ps1
cd ../..

Move-Item -Path "./netuno.cli/protect/out/proguard/netuno.jar" -Destination "./netuno.cli/protect/out/proguard/netuno-setup.jar"

copy "./netuno.cli/pom-base.xml" "./netuno.cli/pom.xml"

Move-Item -Path "./netuno.cli/protect/out/proguard/netuno-base.jar" -Destination "./netuno.cli/protect/out/proguard/netuno.jar"

cd netuno.tritao/protect
./run.ps1
cd ../..

cd bundle
npm install
node index.js
cd ..

cd bundle/out
Compress-Archive -Path netuno -DestinationPath netuno.zip
cd ../..

mkdir -p bundle/dist

copy "./netuno.cli/protect/out/proguard/netuno-setup.jar" "./bundle/dist/netuno-setup.jar"

Move-Item -Path "./bundle/out/netuno.zip" -Destination "./bundle/dist/netuno.zip"
cd bundle
node win.js type=$VersionType
cd ..
