# InvoiceAgent

Receipt management web application on behalf of szamlazz.hu

## Application details

Architecture: JavaEE 8

Frontend: JSF

Application server: JBoss/Wildfly 20.0.1.

### Implemented functionalities:

- action-szamla_agent_nyugta_create
- action-szamla_agent_nyugta_get
- action-szamla_agent_nyugta_storno
- action-szamla_agent_nyugta_send

## Installation

Build with Maven

```sh
$ mvn clean package
```

```sh
# build a custom image
$ docker build -t invoice-agent:1.0 .
# run server
$ docker run -p 8080:8080 invoice-agent:1.0
```
Address: http://localhost:8080/InvoiceAgent-1.0-SNAPSHOT/login.xhtml