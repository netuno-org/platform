
powershell -Command "Invoke-WebRequest -Uri https://github.com/netuno-org/platform/releases/download/stable/netuno.jar -OutFile netuno.jar"

java -jar netuno.jar install

