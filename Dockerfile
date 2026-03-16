# Railway デプロイ用 Dockerfile (リポジトリルートから backend をビルド)
FROM gradle:8.6-jdk21 AS build
WORKDIR /app
COPY backend/ .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p /data
COPY --from=build /app/build/libs/lifelog-backend-all.jar app.jar
EXPOSE 8080
ENV DB_PATH=/data/database.db
ENV ADMIN_PASSWORD=changeme
ENV ADMIN_TOKEN=changeme-token
CMD ["java", "-jar", "app.jar"]
