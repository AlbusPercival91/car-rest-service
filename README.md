# car-rest-service
This is a car rest service providing a REST API for Car Database.

## Rest Api is built using the following technologies:

- Spring Boot 3
- Jakarta EE
- Java 17
- Auth0  
- Docker
- OpenApi V3
- Swagger
- PostgreSQL
- Flyway migration
- Spring Data JPA
- JUnit 5
- DataJpaTest
- WebMvcTest
- Mockito
- Testcontainers
- Lombok

## Required components for launching:
- Docker
- Maven


## Getting started
- Clone the repo: git clone https://github.com/AlbusPercival91/car-rest-service.git
- cd to project root in command line
- Run ```mvn package```, to build .jar executable
- create docker image:
```bash
docker-compose up --build
```
- The application should start and has an output similar to this:
```bash
[...]
INFO 2292 --- [main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.22]
INFO 2292 --- [main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
INFO 2292 --- [main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2485 ms
INFO 2292 --- [main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
INFO 2292 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
INFO 2292 --- [main] com.lyra.sdk.server.ServerApplication    : Started ServerApplication in 3.978 seconds (JVM running for 4.612)

```
- By default the application run on 8080 port.
- Visit any endpoint or http://localhost:8080/api/v1/swagger-ui/index.html 

## Security concerns
This Api uses an Auth0 in order to provide a simple way to secure the Rest API endpoints.



