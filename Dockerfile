FROM jboss/wildfly:21.0.1.Final
ADD target/InvoiceAgent-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/

