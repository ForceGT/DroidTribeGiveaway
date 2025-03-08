FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . /app/
RUN ./gradlew :server:build --no-daemon

FROM openjdk:17-slim

WORKDIR /app
COPY --from=build /app/server/build/libs/*.jar /app/server.jar

ENV PORT=8080
ENV HOST=localhost

EXPOSE 8080

CMD ["java", "-jar", "/app/server.jar"]