
powershell -Command "Invoke-WebRequest -Uri https://github.com/netuno-org/platform/releases/download/stable/netuno-setup.jar -OutFile netuno-setup.jar"

java -jar netuno-setup.jar install

