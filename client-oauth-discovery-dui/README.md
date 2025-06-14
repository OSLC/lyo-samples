# README

## OSLC Client Authentication, Service Discovery & Delegated UIs

This is a sample OSLC client application that explores the different client authentication alternatives of (1) oauth (2) basic authentication (3) no authentication.

The application also demonstrates how to discover OSLC services.

Finally, it demonstrates how to integrate Delegated-UI iframes into your own web-based application.

## Getting started

Run the adaptor as a regular web application:

```bash
mvn clean jetty:run-war
```

> **Note:** Do NOT use `mvn jetty:run` as it will fail with Jetty 12 due to classpath scanning issues.

The application will start on http://localhost:8081/

You can then explore the server starting with http://localhost:8081/services/discovery

If you do not have an OSLC server at hand:

- run the [OSLC Reference Implementation](https://github.com/oslc-op/refimpl)
- use `http://localhost:8800/services/rootservices` as the root services URI