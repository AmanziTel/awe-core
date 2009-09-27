targetDirectory=${HOME}/udig-sdk
equinoxVersion=1.0.1.R33x_v20080118
pdeBuildVersion=3.3.2.v20071019

java -jar ${targetDirectory}/plugins/org.eclipse.equinox.launcher_${equinoxVersion}.jar -application org.eclipse.ant.core.antRunner -buildfile $HOME/.hudson/jobs/awe/workspace/awe/org.amanzi.awe.build/build.xml


