# README

## OSLC Client Authentication, Service Discovery & Delegated UIs

This is a sample OSLC client application that explores the different client authentication alternatives of (1) oauth (2) basic authentication (3) no authentication.

The application also demonstrates how to discover OSLC services.

Finally, it demonstrates how to integrate Delegated-UI iframes into your own web-based application.

## Getting started

### Running with Maven

Run the adaptor as a regular web application:

```bash
mvn clean jetty:run-war
```

> **Note:** Do NOT use `mvn jetty:run` as it will fail with Jetty 12 due to classpath scanning issues.

The application will start on http://localhost:8081/

### Running with Docker

You can also run the application using Docker:

```bash
# Build the Docker image
docker build -t oslc-oauth-discovery-ui .

# Run the container
docker run -p 8080:8080 oslc-oauth-discovery-ui
```

The application will be available at http://localhost:8080/

### Pre-built Docker Images

Pre-built Docker images are available from GitHub Container Registry:

```bash
# Run the latest version
docker run -p 8080:8080 ghcr.io/oslc/lyo-samples/client-oauth-discovery-dui:latest

# Run a specific version
docker run -p 8080:8080 ghcr.io/oslc/lyo-samples/client-oauth-discovery-dui:1.2.3
```

## Exploring the Application

You can then explore the server starting with http://localhost:8081/ (Maven) or http://localhost:8080/ (Docker)

Navigate to the discovery page to get started.

If you do not have an OSLC server at hand:

- run the [OSLC Reference Implementation](https://github.com/oslc-op/refimpl)
- use `http://localhost:8800/services/rootservices` as the root services URI