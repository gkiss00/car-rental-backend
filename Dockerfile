# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

# Resolve dependencies first so this layer is cached across source-only changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render/Railway inject PORT at runtime; application.yml reads it via ${PORT:8080}.
EXPOSE 8080

# Java 11+'s default TLS 1.3 handshake is mishandled by MongoDB Atlas's shared-tier
# TLS-routing proxy from some hosting platforms, causing a "fatal alert: internal_error".
# Forcing TLS 1.2 is MongoDB's own documented workaround.
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "app.jar"]
