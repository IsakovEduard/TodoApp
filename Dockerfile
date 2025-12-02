# ====== BUILD STAGE ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom + download dependencies first (cached)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy source code
COPY src ./src

# Build the project
RUN mvn -q clean package -DskipTests

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:17-jdk
WORKDIR /app

RUN useradd -m springuser
USER springuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]