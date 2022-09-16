FROM ghcr.io/minienw/validation_service:latest_noconfig

RUN adduser validator -H -D
USER validator

WORKDIR /service-bin
COPY application.properties .
COPY identity.json .
ENTRYPOINT ["java","-jar","validationservice-0.0.2-SNAPSHOT.jar","--spring.profiles.active=default"]
