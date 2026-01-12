Clear-Host

function show-netuno{
    Write-Host "
    _   _ ______ _______ _    _ _   _  ____  
   | \ | |  ____|__   __| |  | | \ | |/ __ \ 
   |  \| | |__     | |  | |  | |  \| | |  | |
   | .   |  __|    | |  | |  | | .   | |  | |
   | |\  | |____   | |  | |__| | |\  | |__| |
   |_| \_|______|  |_|   \____/|_| \_|\____/ 
   " -ForegroundColor Red
}

show-netuno

# Silent checks
$isChocoInstalled = Get-Command choco -ErrorAction SilentlyContinue
$mvnInstalled     = Get-Command mvn   -ErrorAction SilentlyContinue
$nodejsInstalled  = Get-Command node  -ErrorAction SilentlyContinue
$javaCmd          = Get-Command java  -ErrorAction SilentlyContinue

# Silent Java version check (GraalVM >= 25)
$javaOk = $false
if ($javaCmd) {
    try {
        $javaRaw = & java -version 2>&1 | Select-Object -First 1
        if ($javaRaw -match 'version\s+"(\d+)\.(\d+)\.(\d+).*"') {
            $javaMajor = [int]$matches[1]
            $javaMinor = [int]$matches[2]
            $javaPatch = [int]$matches[3]

            if ($javaMajor -ge 25 -and $javaRaw -match 'GraalVM') {
                $javaOk = $true
            }
        }
    } catch {
        $javaOk = $false
    }
} 


Write-Host -ForegroundColor Yellow "Checking dependencies:`n"

Write-Host ("`t[Maven]  "  + ($(if ($mvnInstalled)  { "Installed" } else { "Missing" })))
Write-Host ("`t[Java]   "  + ($(if ($javaOk)        { "Installed" } else { "Missing" })))
Write-Host ("`t[NodeJS] "  + ($(if ($nodejsInstalled){ "Installed" } else { "Missing" })))

if (!$javaOk -or !$mvnInstalled -or !$nodejsInstalled) {

    Write-Host -ForegroundColor Yellow "`nWould you like to automatically install the missing dependencies?"
    $installAutomatically = Read-Host "(y - yes | n - no)"

    if ($installAutomatically -notin @("y","Y")) {
        Write-Host -ForegroundColor Red "All dependencies are required. Exiting script..."
        exit
    }

    if (-not $isChocoInstalled) {
        Write-Host -ForegroundColor Red "Chocolatey is not installed. It is required.`nInstall Chocolatey now?"
        $installChoco = Read-Host "(y - yes | n - no)"

        if ($installChoco -notin @("y","Y")) {
            Write-Host -ForegroundColor Red "Chocolatey is required. Exiting script..."
            exit
        }

        Write-Host -ForegroundColor Yellow "Installing Chocolatey..."
        Set-ExecutionPolicy Bypass -Scope Process -Force
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
        iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
        Write-Host -ForegroundColor Green "Chocolatey installed."
    }

    if (!$javaOk)        { choco install graalvm --version=25.0.1 -y }
    if (!$nodejsInstalled){ choco install nodejs-lts --force -y }
    if (!$mvnInstalled) { choco install maven --version=3.8.1 --force -y }

    Write-Host -ForegroundColor Yellow "`nInstallation complete. Restart computer now?"
    $restart = Read-Host "(y - yes | n - no)"

    if ($restart -in @("y","Y")) {
        Restart-Computer
    }
}

Write-Host -ForegroundColor Yellow "`nAll dependencies are installed.`nContinuing with the script..."
Start-Sleep -Seconds 2

do {
    Clear-Host
    show-netuno

    Write-Host -ForegroundColor Yellow "1. Setting Project."
    Write-Host -ForegroundColor Yellow "2. Generate Bundle."
    Write-Host -ForegroundColor White  "`nQ. Press Q to quit."

    $selection = Read-Host "Select option"

    switch ($selection) {

        '1' {
    Write-Host "`nNETUNO - Workspace Setup`n"

    # Unblock all PowerShell scripts
    Get-ChildItem -Filter "*.ps1" | ForEach-Object {
        Unblock-File $_.FullName -ErrorAction SilentlyContinue
    }

    Get-ChildItem -Path "bundle" -Filter "*.ps1" -Recurse | ForEach-Object {
        Unblock-File $_.FullName -ErrorAction SilentlyContinue
    }

    # Install npm dependencies
    Push-Location "bundle"
    npm install
    Pop-Location

    # Set base path for Netuno classes (using old script path)
    $basePath = ".\bundle\base\core\web\WEB-INF\classes\org\netuno"
    New-Item -ItemType Directory -Path $basePath -Force | Out-Null

    Push-Location $basePath

    # Proteu symbolic link
    $proteuTarget = ".\netuno.proteu\target\classes\org\netuno\proteu"
    if (-not (Test-Path "proteu")) {
        New-Item -ItemType SymbolicLink -Name "proteu" -Target $proteuTarget
    }

    # Tritao symbolic link
    $tritaoTarget = ".\netuno.tritao\target\classes\org\netuno\tritao"
    if (-not (Test-Path "tritao")) {
        New-Item -ItemType SymbolicLink -Name "tritao" -Target $tritaoTarget
    }
    
    Pop-Location
        }

        '2' {
            Push-Location "bundle"
            .\publish.ps1
            Pop-Location
        }
    }

} until ($selection -eq 'q')
