# OSLC4J "Test" Sample application

> Sample code extracted from `lyo.core` repo. Code from ca. 2013.

## Getting started

1. Make sure oslc4j-registry is running at http://localhost:8080/OSLC4JRegistry

2. Run:

```
mvn clean install
mvn -f oslc4j-test-sample clean jetty:run-exploded
```

3. Dereference http://localhost:8080/OSLC4JTest/tests:

```
curl --location --request GET 'http://localhost:8080/OSLC4JTest/tests' \
--header 'Accept: text/turtle;q=1.0,application/rdf+xml;q=0.9,application/ld+json;q=0.8,application/n-triples;q=0.3'
```
