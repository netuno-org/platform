# Stop on first error
$ErrorActionPreference = "Stop"

# Go to parent directory
Set-Location ..

# Array de opções para doc.plainMarkdown
$docOptions = @($false, $true)

foreach ($plain in $docOptions) {

    Write-Host "Running Maven tests with -Ddoc.plainMarkdown=$plain"

    mvn `
      -am `
      -pl netuno.tritao `
      "-Drevision=DEV" `
      "-Dtest=BuildLibraryTest" `
      "-Dsurefire.failIfNoSpecifiedTests=false" `
      "-Ddoc.plainMarkdown=$plain" `
      test

    Set-Location netuno.tritao

    if ($plain) {
        $target = "../../doc/doc-plain/"
    } else {
        $target = "../../doc"
    }

    Write-Host "Copying docs to $target"

    function Remove-MdFiles($path) {
        if (Test-Path $path) {
            Remove-Item $path -Force
        }
    }

    # Diretórios de destino
    $dirs = @(
        "$target/docs/library/objects",
        "$target/docs/library/resources",
        "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/objects",
        "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/resources"
    )

    # Criar diretórios se não existirem
    foreach ($dir in $dirs) {
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Force -Path $dir | Out-Null
        }
    }

    # Remover arquivos antigos
    Remove-MdFiles "$target/docs/library/objects/*.md"
    Remove-MdFiles "$target/docs/library/resources/*.md"
    Remove-MdFiles "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/objects/*.md"
    Remove-MdFiles "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/resources/*.md"

    # Copiar arquivos
    Copy-Item "docs/EN/library/objects/*.md" `
              "$target/docs/library/objects/" `
              -Force
    Copy-Item "docs/EN/library/resources/*.md" `
              "$target/docs/library/resources/" `
              -Force

    Copy-Item "docs/PT/library/objects/*.md" `
              "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/objects/" `
              -Force
    Copy-Item "docs/PT/library/resources/*.md" `
              "$target/i18n/pt/docusaurus-plugin-content-docs/current/library/resources/" `
              -Force

    Set-Location ..
}
