# Containerized test execution environment for REST Assured Automation Framework
FROM eclipse-temurin:22-jdk-alpine

WORKDIR /app

# Install bash and curl for utility scripts if needed
RUN apk add --no-cache bash curl

# Copy Maven wrapper files and build descriptors first for optimal layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml checkstyle.xml ./

# Ensure wrapper script is executable
RUN chmod +x mvnw

# Pre-fetch dependencies in non-interactive batch mode
RUN ./mvnw dependency:go-offline -B || true

# Copy framework source code, test suites, and configurations
COPY src/ src/
COPY testng*.xml ./

# Default entrypoint runs Maven test target
ENTRYPOINT ["./mvnw", "test"]
CMD ["-Denv=dev"]
