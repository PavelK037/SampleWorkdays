# ── Stage 1: Build (optional if Maven runs in pipeline) ──────
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Build-time labels for traceability
ARG APP_VERSION=unknown
ARG BUILD_NUMBER=unknown
ARG GIT_COMMIT=unknown

LABEL maintainer="devops@myorg.com"
LABEL app.version="${APP_VERSION}"
LABEL build.number="${BUILD_NUMBER}"
LABEL git.commit="${GIT_COMMIT}"

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy artifact from build stage (or from pipeline build)
COPY --from=builder /build/target/*.jar app.jar
# If pipeline already built the jar:
# COPY target/*.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# ── Health check at container level (backup to K8s probes) ───
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]