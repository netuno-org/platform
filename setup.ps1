Clear-Host
function show-netuno{
    Write-Host "
    _   _ ______ _______ _    _ _   _  ____  
   | \ | |  ____|__   __| |  | | \ | |/ __ \ 
   |  \| | |__     | |  | |  | |  \| | |  | |
   | .   |  __|    | |  | |  | | .   | |  | |
   | |\  | |____   | |  | |__| | |\  | |__| |
   |_| \_|______|  |_|   \____/|_| \_|\____/ 
   " -ForegroundColor red
}

show-netuno

$isChocoInstalled = Get-Command choco -ErrorAction SilentlyContinue

$javaVersion = & java -version 2>&1 | Out-String
$javaVersion = $javaVersion.Contains("version `"11.")

$mvnInstalled = Get-Command mvn -ErrorAction SilentlyContinue
$nodejsInstalled = Get-Command node.exe -ErrorAction SilentlyContinue

$proguardIsInstalled = Join-Path -Path $PSScriptRoot -ChildPath "proguard"
$proguardIsInstalled = Test-Path -Path $proguardIsInstalled -PathType Container

Write-Host -ForegroundColor Yellow "Checking dependencies:`n"

if (!$mvnInstalled) {
    Write-Host "`t[Maven]" -ForegroundColor Red "Missing"
} else {
    Write-Host "`t[Maven]" -ForegroundColor Green "Installed"
}

if (!$javaVersion) {
    Write-Host "`t[Java]" -ForegroundColor Red "Missing"
} else {
    Write-Host "`t[Java]" -ForegroundColor Green "Installed"
}

if (!$nodejsInstalled) {
    Write-Host "`t[NodeJS]" -ForegroundColor Red "Missing"
} else {
    Write-Host "`t[NodeJS]" -ForegroundColor Green "Installed"
}

if (!$proguardIsInstalled) {
    Write-Host "`t[ProGuard]" -ForegroundColor Red "Missing"
} else {
    Write-Host "`t[ProGuard]" -ForegroundColor Green "Installed"
}

if (!$javaVersion -or !$mvnInstalled -or !$nodejsInstalled -or !$proguardIsInstalled) {
    Write-Host -ForegroundColor Yellow "`nWould you like to automatically install the missing dependencies?"
    $installAutomatically = read-Host "(y - yes | n - no)" 
    if($installAutomatically -eq "y" -or $installAutomatically -eq "Y"){
        if (!$isChocoInstalled) {
            Write-Host -ForegroundColor red "Chocolatey is not installed on your system and is required to install dependencies.`nDo you want to install Chocolatey now?" 
            $installChoco = Read-Host -Prompt "(y - yes | n - no)" 
            if ($installChoco -eq "y" -or $installChoco -eq "Y") {
                Write-Host -ForegroundColor Yellow "Installing Chocolatey..."
                Set-ExecutionPolicy Bypass -Scope Process -Force; `
                [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; `
                iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
                Write-Host -ForegroundColor Green "Chocolatey has been installed successfully."
            } else {
                Write-Host -ForegroundColor Red "Chocolatey is required to install dependencies. Exiting script..."
                exit
            }
        } else {
            Write-Host -ForegroundColor Green "Chocolatey is already installed on your system."
        }

        if(!$javaVersion) {
            choco install adoptopenjdk11 --force -y
        }

        if(!$nodejsInstalled) {
            choco install nodejs-lts --force -y
        }

        if(!$mvnInstalled) {
            choco install maven --version=3.8.1 --force -y
        }

        if(!$proguardIsInstalled) {
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            $WebClient = New-Object System.Net.WebClient
            $WebClient.DownloadFile("https://github.com/Guardsquare/proguard/releases/download/v7.2.2/proguard-7.2.2.zip", "$PSScriptRoot\proguard.zip")
            Expand-Archive -LiteralPath "proguard.zip" -DestinationPath ".\" -Force
            Get-ChildItem -Path "./" -Directory "proguard-*" | Rename-Item -NewName "proguard"
            Remove-Item ".\proguard.zip" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            Get-ChildItem -Path ".\" -Directory "proguard-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false
        }

        if(!$javaVersion -or !$mvnInstalled -or !$nodejsInstalled){
            Write-Host -ForegroundColor yellow "`nThe installation is complete. Do you want to restart your computer?"
            $restart = Read-Host "(y - yes | n - no)" 
            if ($restart -eq "y" -or $restart -eq "Y") {
                Restart-Computer
            }
        }
    } else {
        Write-Host -ForegroundColor Red "All dependencies is required. Exiting script..."
        exit
    }
}

Write-Host -ForegroundColor yellow "`nAll dependencies are installed.`nContinuing with the script..."
Start-Sleep -Seconds 2


do {
    Clear-Host
    show-netuno
    Write-Host -ForegroundColor yellow "1. Setting Project."
    Write-Host -ForegroundColor yellow "2. Generate Bundle."
    Write-Host -ForegroundColor white "`nQ. Press Q to quit."

    $selection = Read-Host "Select option"


    switch ($selection)
    {
        '1' {
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

            if(!(Test-Path -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\proteu")){
                New-Item -ItemType SymbolicLink -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\proteu" -Target ".\netuno.proteu\target\classes\org\netuno\proteu"
            }

            if(!(Test-Path -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\tritao")){
                New-Item -ItemType SymbolicLink -Path ".\bundle\base\web\WEB-INF\classes\org\netuno\tritao" -Target ".\netuno.tritao\target\classes\org\netuno\tritao"
            }

            Write-Host ""
            Write-Host "To update files permissions..."
            $User = Read-Host -Prompt 'Enter your normal user: '
            Get-ChildItem -Path . -Recurse -Force | Set-NTFSOwner -Account $User
            Write-Host ""
            pause

        }
        '2' {
            cd .\bundle
            .\publish.ps1
        }
    }
}until ($selection -eq 'q')