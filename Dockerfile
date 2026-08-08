FROM gradle:jdk21 AS build
WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon

COPY src ./src
ARG SENTRY_AUTH_TOKEN
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
