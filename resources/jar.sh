#!/bin/bash
echo "Creating jar file for $1"
cd $2
{ mvn compile; } &
wait
echo "done mvn $2"
pwd
cd $2/target/classes
jar cf ../$1.jar .
cp ../$1.jar $HOME/Documents/Processing3/libraries/$1/library
echo "$1 compiled on $(date)"
