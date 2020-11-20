# Use the official maven/Java 8 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.6-jdk-11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

FROM jetty:9-jre11-slim

# WARNING DO NOT CHANGE WORKDIR or set it back to what it was before
# $JETTY_BASE must be correct before starting Jetty
COPY --from=builder /app/target/*.war /var/lib/jetty/webapps/ROOT.war

# Run the web service on container startup.
# CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/helloworld.jar"]
