# Development — Lyo Client Samples

A collection of Eclipse Lyo client sample applications. The modules target **Lyo 7.0.0**
(Jakarta REST / JAX-RS 3.1) and are independent — there is **no aggregator POM**,
so each module is built and tested on its own.

## Prerequisites

- JDK 21
- Maven 3

## Building & testing

Modules are independent; run Maven from the module directory:

    # lyo-client-samples — unit tests (MockServer-based, no live Jazz/Polarion needed)
    cd lyo-client-samples && mvn test

    # other modules — compile check only (no automated tests)
    cd client-oauth-discovery-dui && mvn test
    cd oauth-twolegged-sample/oauth-cli-helper && mvn test
    cd oauth-twolegged-sample/oauth.sample && mvn test

> This repo has **no Docker-dependent tests**, so `mvn test` is sufficient everywhere.

## Running the discovery sample as a web app

    cd client-oauth-discovery-dui
    mvn clean jetty:run-exploded

Then open http://localhost:8081/discovery/services/discovery
