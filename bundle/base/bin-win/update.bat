@echo off

cd ..
if exist netuno-bundle.zip (
	del netuno-bundle.zip
	java -jar netuno.jar install
) else (
	java -jar netuno.jar install
)