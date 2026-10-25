FROM node:22-alpine AS frontend
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:21-jdk AS backend
WORKDIR /app
COPY backend/ ./
COPY --from=frontend /app/dist src/main/resources/static
RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:21-jre
ARG GIT_SHA=dev
ENV APP_VERSION=${GIT_SHA}
WORKDIR /app
COPY --from=backend /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar" ]