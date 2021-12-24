## Getting started

Prerequisites: have Jena 3 installed under `~/opt/jena3/`.

In one terminal:

    cd oauth.sample
    ~/opt/jena3/bin/tdbloader --loc=RDFStore/ data/consumer-dummy.ttl
    mvn clean jetty:run-exploded

In another terminal:

    mvn -f oauth-cli-helper clean package
    java -jar oauth-cli-helper/target/oauth-cli-helper.jar http://localhost:8080/sample/rest/hello
