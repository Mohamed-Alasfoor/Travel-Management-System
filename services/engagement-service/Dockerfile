FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY gateway/pom.xml gateway/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/travel-service/pom.xml services/travel-service/pom.xml
COPY services/payment-service/pom.xml services/payment-service/pom.xml
COPY services/engagement-service/pom.xml services/engagement-service/pom.xml
RUN mvn -q -pl services/engagement-service -am dependency:go-offline
COPY services/engagement-service/src services/engagement-service/src
RUN mvn -q -pl services/engagement-service -am -DskipTests package
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 travelplan
COPY --from=build /workspace/services/engagement-service/target/engagement-service-*.jar /app.jar
USER travelplan
EXPOSE 8084
ENTRYPOINT ["java","-jar","/app.jar"]
