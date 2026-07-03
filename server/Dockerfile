# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY server/pom.xml .
RUN mvn dependency:go-offline -B
COPY server/src ./src
RUN mvn package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENV PORT=8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
