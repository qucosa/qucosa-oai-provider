FROM maven:3.5.0-jdk-8-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B clean package -DskipTests

FROM openjdk:8-jdk-alpine
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.0/wait /wait
RUN chmod +x /wait
WORKDIR /app
COPY --from=builder ./app/target/qucosa-oai-provider-0.0.1-SNAPSHOT.jar .
CMD /wait && java -jar qucosa-oai-provider-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:application-docker.properties
EXPOSE 8080