FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY gateway/pom.xml gateway/pom.xml
COPY services/user-service/pom.xml services/user-service/pom.xml
COPY services/travel-service/pom.xml services/travel-service/pom.xml
COPY services/payment-service/pom.xml services/payment-service/pom.xml
COPY services/engagement-service/pom.xml services/engagement-service/pom.xml
RUN mvn -pl gateway -am dependency:go-offline
COPY gateway/src gateway/src
RUN mvn -pl gateway -am package -DskipTests

FROM eclipse-temurin:25-jre
RUN useradd --system --uid 10001 travelplan
USER travelplan
COPY --from=build /workspace/gateway/target/gateway-*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
