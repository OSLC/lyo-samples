# Eclise Lyo Client - Sample Code

[![](https://img.shields.io/badge/project-Eclipse%20Lyo-blue?color=418eeb)](https://github.com/eclipse/lyo)
[![CI
(client)](https://github.com/OSLC/lyo-samples/actions/workflows/maven-client.yml/badge.svg)](https://github.com/OSLC/lyo-samples/actions/workflows/maven-client.yml)
[![CI](https://github.com/OSLC/lyo-samples/actions/workflows/maven-oauth-dui.yml/badge.svg)](https://github.com/OSLC/lyo-samples/actions/workflows/maven-oauth-dui.yml)
[![CI](https://github.com/OSLC/lyo-samples/actions/workflows/maven-oauth-twolegged.yml/badge.svg)](https://github.com/OSLC/lyo-samples/actions/workflows/maven-oauth-twolegged.yml)
![Discourse
users](https://img.shields.io/discourse/users?color=28bd84&server=https%3A%2F%2Fforum.open-services.net%2F)

You can find more resources for developing OSLC applications with Lyo, under the
[OSLC Developer
Guide](http://oslc.github.io/developing-oslc-applications/eclipse_lyo/eclipse-lyo.html).

You are also welcome to contact the development team via [lyo-dev mailing
list](https://dev.eclipse.org/mailman/listinfo/lyo-dev)

> **Please note:** Most of code in this repo is not always up to date. You are
> welcome to [contribute](https://github.com/eclipse/lyo#contributing) fixes and
> suggestions.

## Selected Project Descriptions

Below is a selected subset of projects in this repository.

The latest samples target the Lyo release 7.0.0.Alpha3 (Jakarta REST / JAX-RS
3.1), and no longer depend on any particular implementation of JAX-RS. This
gives the developer the chance to adopt any preferred implementation such as
[Jersey](https://jersey.github.io/), [RESTEasy](https://resteasy.github.io/),
etc.

Earlier samples targetting Lyo 2.4.0 (and earlier) supports JAX-RS 1.0, and
assumes the [Apache Wink
implementation](https://svn.apache.org/repos/infra/websites/production/wink/content/index.html).

### [lyo-client-samples](https://github.com/OSLC/lyo-samples/tree/master/lyo-client-samples) - Sample code for Lyo 7.0.0.Alpha3 (Jakarta REST / JAX-RS 3.1)

These are client samples for working with:

- Jazz EWM
- Jazz ERM (aka DNG aka DOORS NG)
- Jazz ETM

using both Basic and Jazz Forms (JFS) auth.

### [OSLC Client Authentication, Service Discovery & Delegated UIs](https://github.com/OSLC/lyo-samples/tree/master/client-oauth-discovery-dui) - Sample code for Lyo 7.0.0.Alpha3 (Jakarta REST / JAX-RS 3.1)

This OSLC client application

- Explores the different client authentication alternatives of (1) oauth (2)
  basic authentication (3) no authentication.
- Demonstrates how to discover OSLC services.
- Demonstrates how to integrate Delegated-UI iframes into your own web-based
  application.

To run the adaptor as a regular web application, run:

    mvn clean jetty:run-exploded

You can then explore the server starting with
http://localhost:8081/discovery/services/discovery

## License

This program and the accompanying materials are made available under the terms
of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 which
accompanies this distribution ([LICENSE](LICENSE) and
[LICENSE.EPL](LICENSE.EPL)).
