FROM maven:3.6.3-jdk-11

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Package the application
RUN mvn clean package

# Run the application
CMD ["mvn", "exec:java"]
