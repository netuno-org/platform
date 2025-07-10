# Navigate up one directory
Set-Location ..

# Run Maven install script
& "./mvn-install.ps1"

# Change to netuno.tritao directory
Set-Location "netuno.tritao"

# Run Maven test
mvn -Dtest=BuildLibraryTest test

# Delete Older files for the English documentation
Remove-Item "../../doc/docs/library/objects/*.md" -Force -ErrorAction SilentlyContinue
Remove-Item "../../doc/docs/library/resources/*.md" -Force -ErrorAction SilentlyContinue

# Delete Older files for the Portuguese documentation
Remove-Item "../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/objects/*.md" -Force -ErrorAction SilentlyContinue
Remove-Item "../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/resources/*.md" -Force -ErrorAction SilentlyContinue

# Copy new EN docs
Copy-Item -Path "docs\EN\library\objects\*.md" -Destination "..\..\doc\docs\library\objects\" -Recurse -Force
Copy-Item -Path "docs\EN\library\resources\*.md" -Destination "..\..\doc\docs\library\resources\" -Recurse -Force

# Copy new PT docs
Copy-Item -Path "docs\PT\library\objects\*.md" -Destination "..\..\doc\i18n\pt\docusaurus-plugin-content-docs\current\library\objects\" -Recurse -Force
Copy-Item -Path "docs\PT\library\resources\*.md" -Destination "..\..\doc\i18n\pt\docusaurus-plugin-content-docs\current\library\resources\" -Recurse -Force