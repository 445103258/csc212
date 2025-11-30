# Multi-stage build for Java Core Application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy source code
COPY java-core/src ./src
COPY python-api/data ./python-api/data

# Create bin directory
RUN mkdir -p bin

# Compile Java files
RUN find src -name "*.java" > sources.txt && \
    javac -d bin @sources.txt

# Final stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy compiled classes and data
COPY --from=builder /app/bin ./bin
COPY --from=builder /app/python-api/data ./python-api/data

# Run the application
CMD ["java", "-cp", "bin", "com.ecommerce.Main"]