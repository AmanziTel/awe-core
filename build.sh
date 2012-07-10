#!/bin/bash
development="development"
testing="testing"
if [ "$1" == development ]
 then
	mvn clean install -f org.amanzi.awe.libs/pom-libs.xml -P development
	mvn clean install -f org.neo4j/pom-libs.xml/ -P development
	mvn clean install -f org.amanzi.testing.libs/pom-libs.xml -P development
 else
	if [ "$1" == testing ]
	 then 
		mvn clean install -f org.amanzi.awe.libs/pom-libs.xml
		mvn clean install -f org.neo4j/pom-libs.xml
		mvn clean install -Ptest
	 else
		mvn clean install -f org.amanzi.awe.libs/pom-libs.xml 
		mvn clean install -f org.neo4j/pom-libs.xml
		mvn clean install -f org.amanzi.testing.libs/pom-libs.xml
		mvn clean install
	fi
fi

