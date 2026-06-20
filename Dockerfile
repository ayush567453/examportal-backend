FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY examserver/pom.xml .
RUN mvn dependency:go-offline -B
COPY examserver/src ./src
RUN mvn clean package -DskipTests -B

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10101
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
