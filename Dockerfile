FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY examserver/pom.xml .
COPY examserver/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10101
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
