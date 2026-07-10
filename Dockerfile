FROM gradle:jdk23 AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
