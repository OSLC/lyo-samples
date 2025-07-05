# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.9-eclipse-temurin-17 as builder

# Copy local code to the container image.
WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies (will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

FROM jetty:9-jre17-eclipse-temurin

# Add metadata
LABEL org.opencontainers.image.title="OSLC Client OAuth Discovery DUI"
LABEL org.opencontainers.image.description="OSLC Client OAuth Discovery Delegated UI Sample"
LABEL org.opencontainers.image.source="https://github.com/eclipse/lyo-samples"
LABEL org.opencontainers.image.vendor="Eclipse Lyo"
LABEL org.opencontainers.image.licenses="EPL-2.0"

# WARNING DO NOT CHANGE WORKDIR or set it back to what it was before
# $JETTY_BASE must be correct before starting Jetty
COPY --from=builder /app/target/*.war /var/lib/jetty/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Run the web service on container startup.
# Jetty will automatically start when the container runs
