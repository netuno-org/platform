
powershell -Command "Invoke-WebRequest -Uri https://github.com/netuno-org/platform/releases/download/latest/netuno.jar -OutFile netuno.jar"

java -jar netuno.jar install version=latest

