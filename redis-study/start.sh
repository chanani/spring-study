echo "Build Project"

./gradlew clean build

echo "Start Server"

cd ./build/libs
java -jar redis-study-0.0.1-SNAPSHOT.jar