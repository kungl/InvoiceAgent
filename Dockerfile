FROM maven:3.8.6-openjdk-8-slim AS build
WORKDIR /source/
COPY . .
RUN mvn clean package

FROM jboss/wildfly:21.0.1.Final
COPY --from=build /source/target/InvoiceAgent-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/
