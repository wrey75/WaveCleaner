#!/bin/bash

#
# This script is for Unix environments and will
# generate the screens. This script is private to
# the project and eventually not part of the
# repository
#

export CLASSPATH=`dirname $0`/../target/classes:$CLASSPATH

echo Generating screeens...

java com.oxande.xmlswing.Xml4Swing -d `dirname $0`/../src/main/java src/main/resources/MainScreen.xml src/main/resources/RecordScreen.xml src/main/resources/ControllerComponent.xml

