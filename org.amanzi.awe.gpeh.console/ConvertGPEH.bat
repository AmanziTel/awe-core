set CP=gt2-main.jar;jflac-1.3.jar;org.eclipse.equinox.common_3.3.0.v20070426.jar;log4j-1.2.8.jar;geoapi.jar
cd bin
java -cp %CP%;. org.amanzi.awe.gpeh.console.test.ProjectRun %1 %2
cd ..
pause