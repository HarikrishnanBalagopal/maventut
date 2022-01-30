#!/usr/bin/env bash

function mybuild() {
    mvn clean package && java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar
}

function myrun() {
    java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar
}

function mydb() {
    docker run --rm -it -p 5432:5432 -e POSTGRES_PASSWORD=mypass1234 postgres
}
