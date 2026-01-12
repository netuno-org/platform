    Get-ChildItem -Filter "*.ps1" | ForEach-Object {
        Unblock-File $_.FullName -ErrorAction SilentlyContinue
    }

    Get-ChildItem -Path "bundle" -Filter "*.ps1" -Recurse | ForEach-Object {
        Unblock-File $_.FullName -ErrorAction SilentlyContinue
    }

    Push-Location "bundle"
    npm install
    Pop-Location

    if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno")){
        New-Item ".\bundle\base\core\web\WEB-INF\classes\org\netuno" -ItemType Directory
    }

    if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\proteu")){
        New-Item -ItemType SymbolicLink -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\proteu" -Target ".\netuno.proteu\target\classes\org\netuno\proteu"
    }

    if(!(Test-Path -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\tritao")){
        New-Item -ItemType SymbolicLink -Path ".\bundle\base\core\web\WEB-INF\classes\org\netuno\tritao" -Target ".\netuno.tritao\target\classes\org\netuno\tritao"
    }
    
    Pop-Location