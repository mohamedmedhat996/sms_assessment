# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project files (the built jar file)
COPY target/sms-0.0.1-SNAPSHOT.jar /app/sms.jar

# Expose the port that the app will run on
EXPOSE 7878

# Run the application
ENTRYPOINT ["java", "-jar", "sms.jar"]