# SMS Project (Student Management System)

## Build and Run the Project with Docker:

### Step 1: Package the Application
Ensure your Spring Boot project is packaged as a JAR. Run the following command to generate the `sms-0.0.1-SNAPSHOT.jar` file in the target directory
### Step 2: Run Docker Compose
Build and run your containers with Docker Compose

```bash
mvn clean package -Dmaven.test.skip
docker-compose up --build