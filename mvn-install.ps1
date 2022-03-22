mvn clean
mvn compile
mvn -"Dmaven.test.skip=true" package
mvn -"Dmaven.test.skip=true" install