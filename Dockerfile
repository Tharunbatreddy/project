# Step 1: Use an official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Step 2: Set the working directory in the container (optional, but it's a good practice)
WORKDIR /app

# Step 3: Copy the JAR file created by Maven from the target directory to the container
COPY ./target/proj-0.0.1-SNAPSHOT.jar user-image.jar

# Step 4: Expose the port your app will run on (usually 8080 for Spring Boot apps)
EXPOSE 8081

# Step 5: Define the command to run your application
ENTRYPOINT ["java", "-jar", "user-image.jar"]