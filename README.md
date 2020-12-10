# Traffic signs server

Spring boot server for a traffic sign recognition application.

## Requirements
For building and running the application you need:

- [JDK 11](http://www.oracle.com/technetwork/java/javase/downloads/)
- [Maven 3.6.0](https://maven.apache.org)

## Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `hu.bme.aut.TrafficSignsApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
./mvnw spring-boot:run
```
To build and package the application run

```shell
./mvnw clean package
```
## Docs
- You may find a detailed OpenAPI documentation for the serven [here](https://app.swaggerhub.com/apis-docs/bizmut32/traffic-signs-server/1.1.0).
- You may find the latest mutation test coverage report [here](https://bizmut32.github.io/traffic-sign-server/).
- This is a server-side application that is part of an application system. Documentation for the system can be found [here](https://github.com/bizmut32/traffic-signs-model).