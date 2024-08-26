FROM amazoncorretto:19.0.2
COPY build/libs/managment_service-1.0.jar /app.jar
COPY src/main/resources/application.yml /application.yml
ENTRYPOINT ["java","-jar","/app.jar"]