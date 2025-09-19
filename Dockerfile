# Use OpenJDK 17
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy input file
COPY file_inputs.txt .

# Expose port 8080
EXPOSE 8080

# Run only web server (disable console interface)
CMD ["java", "-jar", "target/credit_simulator-0.0.1-SNAPSHOT.jar", "file_inputs.txt"]