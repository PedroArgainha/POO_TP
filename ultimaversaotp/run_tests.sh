#!/bin/bash
rm -rf out/*
javac -encoding UTF-8 -cp lib/junit-platform-console-standalone-1.10.2.jar -d out $(find src -name "*.java")
java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path out --scan-class-path
