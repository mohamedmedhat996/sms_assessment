# SMS Project (Student Management System)

## Steps to Build and Run the Project with Docker:

### Step 1: Package the Application
Ensure your Spring Boot project is packaged as a JAR. Run the following command to generate the `sms-0.0.1-SNAPSHOT.jar` file in the target directory:

```bash
mvn clean package -Dmaven.test.skip

### Step 2: Run Docker Compose:
Build and run your containers with Docker Compose:

```bash
docker-compose up --build


## Then Use SMS APIs.postman_collection.json to Test APIs