@echo off
echo "%1"
set lws=libs_without_sources
IF "%1" == %lws%( 
        ECHO ------->install org.amanzi.awe.libs without sources
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml -P %lws%
  	ECHO -------> install org.neo4j without sources
	mvn clean install -f   org.neo4j/pom-libs.xml/ -P %lws%
	ECHO -------> org.jfree without sources
	mvn clean install -f   org.jfree/pom-libs.xml -P %lws%
) ELSE (
 	ECHO ------->install org.amanzi.awe.libs with sources
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml 
  	ECHO -------> install org.neo4j with sources
	mvn clean install -f   org.neo4j/pom-libs.xml
	ECHO -------> org.jfree with sources
	mvn clean install -f   org.jfree/pom-libs.xml 
)
mvn clean install

