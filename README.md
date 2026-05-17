# HR Resource Management System
This project is designed as a simple back-end code of HR resource management system for a company's HR team to use for leave control, employee management and information tracking by utilizing Spring WebFlux framework for reactive programming.

## Technology in This Project
* Java 21
* Spring Boot 3.5
* PostgreSQL 15+
* Docker
* Elasticsearch, Kibana, Filebeat
* Liquibase
* Swagger UI

## Getting Started
### 1. PostgreSQL Database
The application uses postgreSQL for its database so a running instance of a version is required.
First download a version og pgAdmin [here](https://www.pgadmin.org/download/). After creating a user and a password,
in the server create a database called **hr_management**.
Liquibase will automatically create the necessary tables within this database and fill it with dummy data.

#### Database ERD
<img width="598" height="693" alt="Screenshot 2026-05-17 at 22 28 56" src="https://github.com/user-attachments/assets/aaedacdd-40dc-481e-9c1c-926c421e9197" />

### 2. Creation of Application Files
Inside the project there are several example files that need to be created for the application to run. 

First, inside of src/main/resources/ create **application.yml** using the **application.yml.example** file by copying the inside of it. 
Then change:
```
username: // what your username is for postgres user
password: // password for that user
```

Second, create the **application.properties** using the **application.properties.example** file inside the same directory.
Then change:
```
spring.datasource.username=//what your username is for postgres user
spring.datasource.password=// password for that user
```

After the fixes in src/ move to elk-stack/ directory, create the **docker-compose.yml** using the example **docker-compose.yml-example** file.
Inside of this file change:
```
filebeat:
image: docker.elastic.co/beats/filebeat:8.12.2
container_name: filebeat
user: root
volumes:
- /Users/haydar/Desktop/human-resource/logs:/logs:ro // <-- placeholder, change this to your own logs location
- ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml:rw
command: [ "filebeat", "-e" ]
depends_on:
- logstash
- elasticsearch
- kibana
```
by running this in the terminal while on the root of the project:
```
//for Mac and Linux:
pwd

//for Windows:
Get-Location
```
and replace the path up until **/logs:/logs:ro**. Log files will be stored inside the logs/ folder which will be created automatically when the application runs.

Inside the elk-stack/filebeat/ folder create the **filebeat.yml** using the **filebeat.yml-example** file. No need for change in this file when running.

### 3. Starting Docker
For this project to be able to log properly Docker is required. You can download docker [here](https://www.docker.com/). After setting up docker, move to where the **docker-compose.yml** file is, run this in the terminal for the project by:
```
cd elk-stack/

//run docker
docker compose up

//or use this command for it to run in the background
docker compose up -d

// can use this command to check if everything is running
docker ps
```

Wait for all processes to finish.

### 4. Running the Application
Once all the necessary files and the database are created press the **Run** application button on the top of the project to run the spring boot application.
After this the application will be available for usage.

### 5. Stoping the Application
To stop the application click the **Stop** button and close the docker if you wish using the command:
```
docker compose down
```
