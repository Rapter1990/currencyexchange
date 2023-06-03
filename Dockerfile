FROM openjdk:17-jdk-slim AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package

FROM openjdk:17-jdk-slim
WORKDIR currencyexchange
COPY --from=build target/*.jar currencyexchange.jar
ENTRYPOINT ["java", "-jar", "currencyexchange.jar"]