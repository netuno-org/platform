function Show-Menu {
    Clear-Host
    Write-Host "================ NETUNO ================"
    
    Write-Host "1: INSTALL PROGUARD."
    Write-Host "2: INSTALL MAVEN."
    Write-Host "3: SETTING PROJECT."
    Write-Host "Q: PRESS Q TO QUIT."
}

do
 {
    Show-Menu
    $selection = Read-Host "Please make a selection"
    switch ($selection)
    {
    '1' {
        Clear-Host
        if (Test-Path -Path "proguard") {
            Write-Host "Proguard has detected."
        } else {
            Write-Host "Installing ProGuard....." -ForegroundColor Cyan
            Invoke-WebRequest -Uri "https://sourceforge.net/projects/proguard/files/latest/download" -OutFile "proguard.zip" -UserAgent [Microsoft.PowerShell.Commands.PSUserAgent]::FireFox
            Expand-Archive -LiteralPath "proguard.zip" -DestinationPath ".\"
            Get-ChildItem -Path "./" -Directory "proguard-*" | Rename-Item -NewName "proguard"
            Remove-Item "proguard.zip"

            Write-Host "ProGuard installed" -ForegroundColor Green
            Write-Host ""
        }
    } '2' {
        Clear-Host
        $mavenDir = "${env:ProgramFiles(x86)}\Apache"
        if (Test-Path -Path "$mavenDir") {
            Write-Host "The directory needs to be clean. - Maven has Detected."
        } else {
            Write-Host "Installing Apache Maven 3.8.5 ..." -ForegroundColor Cyan

            $mavenDownload = "https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.zip";
            Invoke-WebRequest -Uri $mavenDownload -OutFile "maven.zip" -UserAgent [Microsoft.PowerShell.Commands.PSUserAgent]::FireFox
            Expand-Archive -LiteralPath "maven.zip" -DestinationPath $mavenDir
            Get-ChildItem -Path $mavenDir -Directory "apache-*" | Rename-Item -NewName "Maven"
            Remove-Item "maven.zip"
            Write-Host $apachePath

            $mavenPath = "${mavenDir}\Maven"

            [Environment]::SetEnvironmentVariable("M2_HOME", $mavenPath, "Machine")
            [Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenPath, "Machine")

            Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path
            $old = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path).path
            $new  =  "$old;$mavenPath\bin"
            Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path -Value $new

            Write-Host "Apache Maven 3.8.5 installed" -ForegroundColor Green
            Write-Host "REBOOT THE COMPUTER" -ForegroundColor Red
        }
    } '3' {
        Clear-Host
        cd bundle
        npm install
        cd ..

        mvn clean
        mvn install
        mvn compile

        Write-Host ""
        Write-Host "Setting SymbolicLink ..."

        if(!(Test-Path -Path ".\bundle\base\web\WEB-INF\classes\org\netuno")){
            New-Item ".\bundle\base\web\WEB-INF\classes\org\netuno" -ItemType Directory
        }

        if((Get-Item ".\bundle\base\web\WEB-INF\classes\org\netuno\proteu").LinkType -eq "SymbolicLink"){
            New-Item -ItemType SymbolicLink -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\proteu" -Target ".\netuno.proteu\target\classes\org\netuno\proteu"
        }

        if((Get-Item ".\bundle\base\web\WEB-INF\classes\org\netuno\tritao").LinkType -eq "SymbolicLink"){
           New-Item -ItemType SymbolicLink -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\tritao" -Target ".\netuno.tritao\target\classes\org\netuno\tritao"
        }
       
        Write-Host ""
    }
    }
    pause
 }
 until ($selection -eq 'q')

