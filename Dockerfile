FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Install security-library into the local Maven repo first
COPY security-library/pom.xml ./security-library/pom.xml
COPY security-library/src    ./security-library/src
RUN mvn -f security-library/pom.xml install -DskipTests -B -q

# Build the main application
COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8130
ENTRYPOINT ["java", "-jar", "app.jar"]
