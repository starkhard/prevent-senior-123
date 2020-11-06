FROM openjdk:8
COPY target/prevent-senior-0.0.1-SNAPSHOT.jar  prevent.jar
EXPOSE 8080 8081 8082
ENTRYPOINT ["java","-jar","/prevent.jar"]