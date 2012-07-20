#!/bin/bash
development="development"
testing="testing"
if [ "$1" == development ]
 then
	$M2_HOME/bin/mvn clean install -f org.amanzi.awe.libs/pom-libs.xml -P development
	$M2_HOME/bin/mvn clean install -f org.neo4j/pom-libs.xml/ -P development
	$M2_HOME/bin/mvn clean install -f org.amanzi.testing.libs/pom-libs.xml -P development
 else
	if [ "$1" == testing ]
	 then 
		$M2_HOME/bin/mvn clean install -f org.amanzi.awe.libs/pom-libs.xml
		$M2_HOME/bin/mvn clean install -f org.neo4j/pom-libs.xml
		$M2_HOME/bin/mvn clean install -f org.amanzi.testing.libs/pom-libs.xml
		$M2_HOME/bin/mvn clean install -P test -Dmaven.test.skip
	 else
		$M2_HOME/bin/mvn clean install -f org.amanzi.awe.libs/pom-libs.xml 
		$M2_HOME/bin/mvn clean install -f org.neo4j/pom-libs.xml
		$M2_HOME/bin/mvn clean install
	fi
fi
