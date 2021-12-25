## Getting started

Prerequisites: Register for Jazz.net or run your own instance or run your own IBM Jazz server installation.

If you registered on the Jazz.net, you will need to use basic auth, use the flag `--basic` for that (replace `YourUserName` with your login):

```
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.oslc4j.client.samples.EWMSample" -Dexec.args="-url https://jazz.net/sandbox02-ccm/ \
-user YourUserName -password \"s3cret\" \
-project \"YourUserName Project (Change and Architecture Management)\" --basic"
```

For form-based auth, use the following command (replace `YourUserName` with your login and replace the URL with the URL of your installation):

```
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.oslc4j.client.samples.EWMSample" -Dexec.args="-url https://nordic.clm.ibmcloud.com/ccm/ \
-user YourUserName \
-password \"s3cret\" \
-project \"OSLC Environment (EWM)\""
```

See [old docs](../xUnmaintained/lyo-4.1/oslc-java-samples/README.md) for additional information.

---

Also see https://github.com/oslc-op/refimpl/tree/master/src/client-toolchain