#!/bin/sh
targetDirectory=${HOME}/build/udig-sdk
equinoxVersion=1.0.1.R33x_v20080118
pdeBuildVersion=3.3.2.v20071019

java -jar ${HOME}/build/udig-sdk/plugins/org.eclipse.equinox.launcher_1.0.1.R33x_v20080118.jar -application org.eclipse.ant.core.antRunner -buildfile $HOME/.hudson/jobs/awe/workspace/awe/org.amanzi.awe.build/build.xml


