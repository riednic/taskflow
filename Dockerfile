FROM gradle:9.5.1-jdk21 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle bootJar --no-daemon -x test


FROM eclipse-temurin:21-jre

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]