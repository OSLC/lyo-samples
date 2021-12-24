# OSLC4J Stockquote sample

> Sample code extracted from `lyo.core` repo. This code is from ca. 2013.

## Getting started

Run:

```
mvn clean install
mvn -f oslc4j-stockquote-sample clean jetty:run-exploded
```

Dereference http://localhost:8080/OSLC4JStockQuote/stockQuotes:

```
curl --location --request GET 'http://localhost:8080/OSLC4JStockQuote/stockQuotes' \
--header 'Accept: text/turtle;q=1.0,application/rdf+xml;q=0.9,application/ld+json;q=0.8,application/n-triples;q=0.3'
```
