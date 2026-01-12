Clear-Host

function show-netuno {
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

# -------------------------------
# Silent checks for dependencies
# -------------------------------
Write-Host -ForegroundColor Cyan "Checking installed dependencies..."
Start-Sleep -Seconds 1

$isChocoInstalled = Get-Command choco -ErrorAction SilentlyContinue
$mvnInstalled     = Get-Command mvn   -ErrorAction SilentlyContinue
$nodejsInstalled  = Get-Command node  -ErrorAction SilentlyContinue
$javaCmd          = Get-Command java  -ErrorAction SilentlyContinue
$sevenZipCmd      = Get-Command 7z    -ErrorAction SilentlyContinue

# -------------------------------
# Robust Java >= 25 check
# -------------------------------
$javaOk = $false
if ($javaCmd) {
    try {
        Write-Host "Checking Java version..."
        $javaRawAll = & java -version 2>&1
        foreach ($line in $javaRawAll) {
            if ($line -match 'version\s+"(\d+)(\.(\d+))?(\.(\d+))?') {
                $javaMajor = [int]$matches[1]
                if ($javaMajor -ge 25) {
                    $javaOk = $true
                    break
                }
            }
        }
    } catch {
        $javaOk = $false
    }
}

# -------------------------------
# Dependency status
# -------------------------------
Write-Host -ForegroundColor Yellow "`nDependency check results:`n"
Write-Host ("`t[Maven]   " + ($(if ($mvnInstalled)  { "Installed" } else { "Missing" })))
Write-Host ("`t[Java]    " + ($(if ($javaOk)        { "Installed" } else { "Missing or < 25" })))
Write-Host ("`t[NodeJS]  " + ($(if ($nodejsInstalled){ "Installed" } else { "Missing" })))
Write-Host ("`t[7-Zip]   " + ($(if ($sevenZipCmd)    { "Installed" } else { "Missing" })))
Start-Sleep -Seconds 2

# -------------------------------
# Install missing dependencies
# -------------------------------
if (!$javaOk -or !$mvnInstalled -or !$nodejsInstalled -or !$sevenZipCmd) {

    Write-Host -ForegroundColor Yellow "`nWould you like to automatically install the missing dependencies?"
    $installAutomatically = Read-Host "(y - yes | n - no)"

    if ($installAutomatically -notin @("y","Y")) {
        Write-Host -ForegroundColor Red "All dependencies are required. Exiting script..."
        exit
    }

    # Install Chocolatey if missing
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
        Write-Host -ForegroundColor Green "Chocolatey installed successfully."
        Start-Sleep -Seconds 2
    }

    # Install missing packages
    if (!$javaOk) {
        Write-Host -ForegroundColor Yellow "Installing Java (GraalVM 25.0.1)..."
        choco install graalvm --version=25.0.1 -y --no-progress
        Write-Host -ForegroundColor Green "Java installed."
        Start-Sleep -Seconds 2
    }
    if (!$nodejsInstalled) {
        Write-Host -ForegroundColor Yellow "Installing NodeJS LTS..."
        choco install nodejs-lts --force -y --no-progress
        Write-Host -ForegroundColor Green "NodeJS installed."
        Start-Sleep -Seconds 2
    }
    if (!$mvnInstalled) {
        Write-Host -ForegroundColor Yellow "Installing Maven 3.8.1..."
        choco install maven --version=3.8.1 --force -y --no-progress
        Write-Host -ForegroundColor Green "Maven installed."
        Start-Sleep -Seconds 2
    }
    if (!$sevenZipCmd) {
        Write-Host -ForegroundColor Yellow "Installing 7-Zip..."
        choco install 7zip -y --no-progress
        Write-Host -ForegroundColor Green "7-Zip installed."
        Start-Sleep -Seconds 2
    }

    Write-Host -ForegroundColor Yellow "`nInstallation complete. Restart computer now?"
    $restart = Read-Host "(y - yes | n - no)"

    if ($restart -in @("y","Y")) {
        Write-Host -ForegroundColor Yellow "Restarting computer..."
        Start-Sleep -Seconds 2
        Restart-Computer
    }
}

Write-Host -ForegroundColor Yellow "`nAll dependencies are installed.`nContinuing with the script..."
Start-Sleep -Seconds 2

# -------------------------------
# Main menu loop
# -------------------------------
do {
    Clear-Host
    show-netuno

    Write-Host -ForegroundColor Yellow "1. Setting Project."
    Write-Host -ForegroundColor Yellow "2. Generate Bundle."
    Write-Host -ForegroundColor White  "`nQ. Press Q to quit."

    $selection = Read-Host "Select option"

    switch ($selection) {

        '1' {
            Write-Host -ForegroundColor Cyan "`nSetting up project..."
            Start-Sleep -Seconds 1

            Get-ChildItem -Filter "*.ps1" | ForEach-Object {
                Unblock-File $_.FullName -ErrorAction SilentlyContinue
            }

            Get-ChildItem -Path "bundle" -Filter "*.ps1" -Recurse | ForEach-Object {
                Unblock-File $_.FullName -ErrorAction SilentlyContinue
            }

            Push-Location "bundle"
            Write-Host -ForegroundColor Cyan "Running npm install in bundle folder..."
            npm install
            Pop-Location

            Write-Host -ForegroundColor Cyan "Creating necessary directories and symbolic links..."
            if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno")){
                New-Item ".\bundle\base\core\web\WEB-INF\classes\org\netuno" -ItemType Directory
            }

            if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\proteu")){
                New-Item -ItemType SymbolicLink -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\proteu" -Target ".\netuno.proteu\target\classes\org\netuno\proteu"
            }

            if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\tritao")){
                New-Item -ItemType SymbolicLink -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\tritao" -Target ".\netuno.tritao\target\classes\org\netuno\tritao"
            }

            Write-Host -ForegroundColor Green "Project setup complete."
            Start-Sleep -Seconds 5
        }

        '2' {
            if (Test-Path "bundle\publish.ps1") {
                Write-Host -ForegroundColor Cyan "`nGenerating bundle..."
                Start-Sleep -Seconds 1
                Push-Location "bundle"
                .\publish.ps1
                Pop-Location
                Write-Host -ForegroundColor Green "Bundle generation complete."
                Start-Sleep -Seconds 5
            } else {
                Write-Host -ForegroundColor Red "publish.ps1 not found in bundle folder."
                Start-Sleep -Seconds 5
            }
        }
    }

} until ($selection -eq 'q' -or $selection -eq 'Q')

Write-Host -ForegroundColor Yellow "`nExiting script. Goodbye!"
Start-Sleep -Seconds 3
