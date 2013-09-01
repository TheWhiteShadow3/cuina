#!/bin/sh
# Script:	start.sh
# Author:	TheWhiteShadow & fireandfuel
# Version:	1.2

CUINA_OPTS="-Dcuina.game.path=../../CuinaEclipse/TestWorkspace/Test -Dcuina.plugin.path=plugins"

echo $CUINA_OPTS
java -Xms512m $CUINA_OPTS -jar cuina.engine.jar
