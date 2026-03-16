# ── Build stage ──────────────────────────────────────────────────
FROM gradle:8.6-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

# ── Runtime stage ─────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create data directory for SQLite
RUN mkdir -p /data

COPY --from=build /app/build/libs/lifelog-backend-all.jar app.jar

EXPOSE 8080

ENV DB_PATH=/data/database.db
ENV ADMIN_PASSWORD=changeme
ENV ADMIN_TOKEN=changeme-token

CMD ["java", "-jar", "app.jar"]
