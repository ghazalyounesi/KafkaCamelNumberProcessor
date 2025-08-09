FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/kafka-camel-example-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
