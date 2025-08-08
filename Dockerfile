  FROM maven:3.8-openjdk-17-slim AS build
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine

COPY --from=build /target/blog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]