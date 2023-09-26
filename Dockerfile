# Use a base image with Maven to build the application
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file to the container
COPY pom.xml .

# Copy the source code to the container
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a new base image with Java to run the application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar ./*.jar

# Expose the port that your Spring application listens on
EXPOSE 8080

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "*.jar"]