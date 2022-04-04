Set-ExecutionPolicy Unrestricted
function show-netuno{
    Write-Host "
    _   _ ______ _______ _    _ _   _  ____  
   | \ | |  ____|__   __| |  | | \ | |/ __ \ 
   |  \| | |__     | |  | |  | |  \| | |  | |
   | .   |  __|    | |  | |  | | .   | |  | |
   | |\  | |____   | |  | |__| | |\  | |__| |
   |_| \_|______|  |_|   \____/|_| \_|\____/ 
   " -ForegroundColor DarkCyan
}
function Show-Menu {
    Clear-Host
    show-netuno
    Write-Host " INSTALLATION SCRIPT V1.0" -ForegroundColor red
    Write-Host ""
    Write-Host " 1: Install Java JDK 11."
    Write-Host " 2: Install Maven."
    Write-Host " 3: Install ProGuard."
    Write-Host " 4: Setting Project."
    Write-Host " 5: Generate Bundle."
    Write-Host " Q: Press Q to quit."
}

do
 {
    $global:ProgressPreference = "SilentlyContinue"
    Show-Menu
 
    $selection = Read-Host "
 Make a select"

    $netunoDir = "${env:ProgramFiles(x86)}\Netuno"
 


    switch ($selection)
    {
    '1' {
        

        Remove-Item "java.zip" -Force -ErrorAction Ignore 
        Get-ChildItem -Path "$netunoDir\java" -Directory "jdk-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false

        $installJdk = "true";
        if(Test-Path -Path "$netunoDir/java"){
            
            Clear-Host
            show-netuno
            Write-Host ""
            Write-Host " Installing AdoptOpenJDK 11..." -ForegroundColor yellow
            Write-Host ""
            
            Write-Host " A previous installation has been identified, do you want to continue?" -ForegroundColor yellow
            $confirmation = Read-Host " [y] - yes(default) | [n] - no"
            if ($confirmation -eq 'n') {
                $installJdk = "false";
            }
        }

        if($installJdk -eq "true"){

            Clear-Host
            show-netuno

            Remove-Item "$netunoDir\java" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            New-Item -Path "$netunoDir\java" -ItemType Directory -Force | Out-Null

            Write-Host ""
            Write-Host " Installing AdoptOpenJDK 11..." -ForegroundColor yellow
            Write-Host ""
            
            Write-Host " Downloading..." -ForegroundColor red
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            $WebClient = New-Object System.Net.WebClient
            $WebClient.DownloadFile("https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.14.1%2B1/OpenJDK11U-jdk_x64_windows_hotspot_11.0.14.1_1.zip", "$PSScriptRoot\java.zip")
            Write-Host " Download Complete." -ForegroundColor green
            
            Write-Host ""
            Write-Host " Extracting file..." -ForegroundColor red
            Expand-Archive -LiteralPath "java.zip" -DestinationPath "$netunoDir\java" -Force
            Get-ChildItem -Path "$netunoDir\java" -Directory "jdk-*" | Rename-Item -NewName "java-11"
            Write-Host " Extracting Complete." -ForegroundColor green


            Write-Host ""
            Write-Host " Adding Windows Environment variables..." -ForegroundColor red
            [Environment]::SetEnvironmentVariable("JAVA_HOME", "$netunoDir\java\java-11", "Machine")
            Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path | Out-Null
            $old = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path).path
         
            if(!($old -like "*$netunoDir\java\java-11\bin*")){
                $new = "$old;$netunoDir\java\java-11\bin"
                Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path -Value $new
            }
         
            Write-Host " Windows Environment variables have been added.." -ForegroundColor green

    
            Write-Host ""
            Write-Host " Cleaning cache files..." -ForegroundColor red
            Remove-Item "java.zip" -Force -ErrorAction Ignore
            Get-ChildItem -Path "$netunoDir\java" -Directory "jdk-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false
            Write-Host " Cleaning Complete." -ForegroundColor green
    
            Write-Host ""
            Write-Host " AdoptOpenJDK 11 installed..." -ForegroundColor yellow
            Write-Host ""
        }else{
            Write-Host ""
            Write-Host " Installation has canceled..." -ForegroundColor red
            Write-Host ""   
        }
        pause
    } '2' {
        $installJdk = "true";
        if(Test-Path -Path "$netunoDir\maven"){
            
            Clear-Host
            show-netuno
            Write-Host ""
            Write-Host " Installing Apache Maven 3.8.5..." -ForegroundColor yellow
            Write-Host ""
            
            Write-Host " A previous installation has been identified, do you want to continue?" -ForegroundColor yellow
            $confirmation = Read-Host " [y] - yes(default) | [n] - no"
            if ($confirmation -eq 'n') {
                $installJdk = "false";
            }
        }

        if($installJdk -eq "true"){

            Clear-Host
            show-netuno

            Remove-Item "$netunoDir\maven" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            Remove-Item "maven.zip" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            Get-ChildItem -Path "$netunoDir" -Directory "apache-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false

            Write-Host ""
            Write-Host " Installing  Apache Maven 3.8.5..." -ForegroundColor yellow
            Write-Host ""
            
            Write-Host " Downloading..." -ForegroundColor red
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            $WebClient = New-Object System.Net.WebClient
            $WebClient.DownloadFile("https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.zip", "$PSScriptRoot\maven.zip")
            Write-Host " Download Complete." -ForegroundColor green
            
            Write-Host ""
            Write-Host " Extracting file..." -ForegroundColor red
            Expand-Archive -LiteralPath "maven.zip" -DestinationPath "$netunoDir" -Force
            Get-ChildItem -Path "$netunoDir" -Directory "apache-*" | Rename-Item -NewName "maven"
            Write-Host " Extracting Complete." -ForegroundColor green


            Write-Host ""
            Write-Host " Adding Windows Environment variables..." -ForegroundColor red
            
            $mavenPath = "${netunoDir}\maven"
            
            [Environment]::SetEnvironmentVariable("M2_HOME", $mavenPath, "Machine")
            [Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenPath, "Machine")

            Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path | Out-Null
            $old = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path).path
         
            if(!($old -like "*$mavenPath\bin*")){
                $new = "$old;$mavenPath\bin"
                Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name path -Value $new
            }
            Write-Host " Windows Environment variables have been added.." -ForegroundColor green

    
            Write-Host ""
            Write-Host " Cleaning cache files..." -ForegroundColor red

            Remove-Item "maven.zip" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            Get-ChildItem -Path "$netunoDir" -Directory "apache-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false
            
            Write-Host " Cleaning Complete." -ForegroundColor green
    
            Write-Host ""
            Write-Host " Apache Maven 3.8.5 installed..." -ForegroundColor yellow
            Write-Host ""
        }else{
            Write-Host ""
            Write-Host " Installation has canceled..." -ForegroundColor red
            Write-Host ""   
        }

        pause
    } '3' {

        Remove-Item ".\proguard.zip" -Recurse -Confirm:$false -Force -ErrorAction Ignore
        Get-ChildItem -Path ".\" -Directory "proguard-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false

        $installProGuard = "true";
        if(Test-Path -Path "proguard"){
            
            Clear-Host
            show-netuno
            Write-Host ""
            Write-Host " Installing ProGuard..." -ForegroundColor yellow
            Write-Host ""
            
            Write-Host " A previous installation has been identified, do you want to continue?" -ForegroundColor yellow
            $confirmation = Read-Host " [y] - yes(default) | [n] - no"
            if ($confirmation -eq 'n') {
                $installProGuard = "false";
            }
        }

        if($installProGuard -eq "true"){

            Remove-Item ".\proguard" -Recurse -Confirm:$false -Force -ErrorAction Ignore

            Clear-Host
            show-netuno
            Write-Host ""
            Write-Host " Installing ProGuard..." -ForegroundColor yellow
            Write-Host ""

            Write-Host " Downloading..." -ForegroundColor red
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            $WebClient = New-Object System.Net.WebClient
            $WebClient.DownloadFile("https://sourceforge.net/projects/proguard/files/latest/download", "$PSScriptRoot\proguard.zip")
            Write-Host " Download Complete." -ForegroundColor green

            Write-Host ""
            Write-Host " Extracting file..." -ForegroundColor red
            Expand-Archive -LiteralPath "proguard.zip" -DestinationPath ".\" -Force
            Get-ChildItem -Path "./" -Directory "proguard-*" | Rename-Item -NewName "proguard"
            Write-Host " Extracting Complete." -ForegroundColor green

            Write-Host ""
            Write-Host " Cleaning cache files..." -ForegroundColor red
            Remove-Item ".\proguard.zip" -Recurse -Confirm:$false -Force -ErrorAction Ignore
            Get-ChildItem -Path ".\" -Directory "proguard-*" | Remove-Item -Force -ErrorAction Ignore -Confirm:$false
            Write-Host " Cleaning Complete." -ForegroundColor green
    
            Write-Host ""
            Write-Host " ProGuard installed..." -ForegroundColor yellow
            Write-Host ""
        }else{
            Write-Host ""
            Write-Host " Installation has canceled..." -ForegroundColor red
            Write-Host ""   
        }
        pause
    } '4' {
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
        pause
    } '5' {
        cd .\bundle
        .\publish.ps1
    }
    }
 }
 until ($selection -eq 'q')

