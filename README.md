# keycloak-spring-boot-rest-angular-demo
Demo for configuring Keycloak authentication for a spring-boot rest service and AngularJs web client

# Prerequisite

- Keycloack (1.1.0) server installed
- Java (1.8)
- Maven (3.2.2)
- Source code

 
# Setup

1. Clone https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo

2. Download **keycloak-appliance-dist-all-1.1.0.final.zip** (or later version).

Start keycloack and import the realm provided with the source code.

3. Start the rest server:

`mvn spring-boot:run`
4. Start the angular application

`mvn spring-boot:run`
5. Go to [localhost:7005](localhost:7005 "localhost:7005") and login using user/pass.

6. Press reload to create a hello-world request to server.

 

# Configuration


As all three applications run on different domains, we have to configure CORS.

## Enable CORS in rest project

In **keycloak.json** a new line has to be added:

`"enable-cors": true`
Also, **CORSFilter** has to be added in order to provide required headers for XMLHttpRequests.

https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo/blob/master/rest/src/main/java/hello/SimpleCORSFilter.java

## AngularJS project configuration

[![](https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo/blob/master/screenshot-2015-05-14-18-21-17.png)](https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo/blob/master/screenshot-2015-05-14-18-21-17.png)
The application must be public. Also, configure the web origin and the matching redirect URL.

## Spring Security Context

 
In some cases we may need to access the user in context. Keycloak configuration is configured at container level, so Spring Security filters cannot be used.

Add security context dependency in order to have **SpringContextHolder** class. Do not use spring-boot-security as  then the web application will be secure by default with ‘basic’ authentication on all HTTP endpoints.

We can add a simple **HttpFilter** which sets the securityContext:

https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo/blob/master/rest/src/main/java/hello/AuthenticationFilter.java

# Tests

For writing integration tests we need Kecloak's Direct Access API.

https://github.com/iuliazidaru/keycloak-spring-boot-rest-angular-demo/blob/master/rest/src/test/java/hello/HelloWorldConfigurationTests.java

 

# Resources

- Keycloak and Tomcat: https://github.com/fabric8io/quickstarts/blob/master/quickstarts/spring-boot/keycloak/src/main/java/io/fabric8/quickstarts/springbootkeycloak/App.java
- Keycloak direcr access API: http://docs.jboss.org/keycloak/docs/1.2.0.CR1/userguide/html/direct-access-grants.html
- Spring boot example project: https://github.com/spring-guides/gs-actuator-service.git
- AngularJs integration with Keycloak: https://github.com/keycloak/keycloak/tree/master/examples/demo-template/angular-product-app
- Spring boot documentation: http://docs.spring.io/spring-boot/docs/1.2.3.RELEASE/reference/htmlsingle/#boot-features-security
