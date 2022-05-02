FROM openjdk:17-alpine
WORKDIR service-bin
COPY build/libs/validationservice-0.0.2-SNAPSHOT.jar .
COPY build/resources/main /service-bin/
EXPOSE 8080
ENTRYPOINT ["java","-jar","validationservice-0.0.2-SNAPSHOT.jar","--spring.profiles.active=default,dockerdemolocal"]