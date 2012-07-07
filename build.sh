#!/bin/sh
echo $1
libs_without_sources="libs_without_sources"
if [ "$1" == development ]
 then
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml -P development
	mvn clean install -f   org.neo4j/pom-libs.xml/ -P development
else
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml 
	mvn clean install -f   org.neo4j/pom-libs.xml
fi
mvn clean install

