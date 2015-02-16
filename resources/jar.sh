cd $HOME/Documents/workspace/midimapper/target/classes
jar cf ../midimapper.jar .
cp ../midimapper.jar $HOME/Documents/Processing/libraries/midimapper/library
echo "midimapper compiled on $(date)"