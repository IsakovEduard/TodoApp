# ====== BUILD STAGE ======
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom + download dependencies first (cached)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy source code
COPY src ./src
COPY frontend ./frontend

# Build the project
RUN mvn -q clean package -DskipTests -DskipFrontendTests=true

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Create a user and data directory BEFORE switching user
RUN useradd -m springuser \
    && mkdir -p /app/data \
    && chown -R springuser:springuser /app/data

# Switch to non-root user
USER springuser

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]