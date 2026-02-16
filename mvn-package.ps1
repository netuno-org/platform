$REVISION = Get-Date -Format "yyyy.MM.dd"

mvn -Drevision="$REVISION" clean

mvn -Drevision="$REVISION" compile -e

mvn -Drevision="$REVISION" -DskipTests=true package -X