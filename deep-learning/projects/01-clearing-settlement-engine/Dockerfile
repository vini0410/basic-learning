# Runtime-only Dockerfile: assumes target/*.jar was built on the host
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the already-built jar produced by 'mvn package'
COPY target/clearing-settlement-engine-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]
