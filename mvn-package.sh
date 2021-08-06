#!/bin/sh

mvn clean && mvn compile && mvn -Dmaven.test.skip=true package

