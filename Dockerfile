FROM gradle:jdk8-alpine as build-env
MAINTAINER Ivan Boyarkin

COPY --chown=gradle ./ /app
WORKDIR /app
ADD --chown=gradle "https://unpkg.com/swagger-ui-dist@3.17.0/swagger-ui-bundle.js" \
      "https://cdn.rawgit.com/swagger-api/swagger-ui/v/3.17.0/dist/swagger-ui.css" \
      /app/src/main/resources/static/
RUN mv openapi.yml /app/src/main/resources/static/ && \
    gradle build

FROM openjdk:8-jre-alpine
COPY --from=build-env /app/build/libs/java-test-default-0.1.0.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]