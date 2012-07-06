#!/bin/sh
echo $1
libs_without_sources="libs_without_sources"
if [ "$1" == libs_without_sources ]
 then
        echo '------->install org.amanzi.awe.libs without sources'
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml -P libs_without_sources
  	echo '-------> install org.neo4j without sources'
	mvn clean install -f   org.neo4j/pom-libs.xml/ -P libs_without_sources
	echo '-------> org.jfree without sources'
	mvn clean install -f   org.jfree/pom-libs.xml -P libs_without_sources
else
 echo '------->install org.amanzi.awe.libs with sources'
	mvn clean install -f   org.amanzi.awe.libs/pom-libs.xml 
  	echo '-------> install org.neo4j with sources'
	mvn clean install -f   org.neo4j/pom-libs.xml
	echo '-------> org.jfree with sources'
	mvn clean install -f   org.jfree/pom-libs.xml 
fi
mvn clean install

