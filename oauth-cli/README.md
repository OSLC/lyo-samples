# Two-legged OAuth Sample

This is a simple example of how to sign requests using two-legged OAuth
in Java. Two-legged OAuth does not require an access token or a user to type in a
password. You simply need the consumer key and consumer secret to sign
the requests. Performing two-legged OAuth is generally preferable to
storing a username and password on the client, although you should treat
the consumer secret like it's a password.

## Getting started

Prerequisites: have Jena 3 installed under `~/opt/jena3/`.

In one terminal:

    cd oauth.sample
    ~/opt/jena3/bin/tdbloader --loc=RDFStore/ data/consumer-dummy.ttl
    mvn clean jetty:run-exploded

In another terminal:

    mvn -f oauth-cli-helper clean package
    java -jar oauth-cli-helper/target/oauth-cli-helper.jar http://localhost:8080/sample/rest/hello


## Customizing the client call

You'll need to set up an OAuth consumer on the server you want
to connect to. If using CLM, you'll want to set up an OAuth Consumer
with a functional user.

- Edit
  org.eclipse.lyo.client.java.oauth.sample/src/main/resources/oauth.properties
  and set your consumer key and secret.
- Run these commands to try the sample:

```
mvn install
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.client.oauth.sample.OAuthClient" -Dexec.args="<uri_to_connect_to>"
```