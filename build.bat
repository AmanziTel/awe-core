@echo off
echo "%1"
set lws=development
IF "%1" == %lws%( 
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml -P %lws%
	mvn clean install -f   org.neo4j/pom-libs.xml/ -P %lws%
) ELSE (
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml 
	mvn clean install -f   org.neo4j/pom-libs.xml
)
mvn clean install

