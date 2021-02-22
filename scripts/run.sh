#!/bin/bash

MAINCLASS="ru.tehdmitry.Main"
JAR="TehPrinterCalibrator-1.0-SNAPSHOT.jar"
HOME="."

JAVA="java"

cd $HOME
LANG=en_US.UTF-8 $JAVA -cp "$JAR:lib/*" -Xms256m ${MAINCLASS}
