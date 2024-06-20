# E-Wallet Application with Rabbit MQ

## Technology Stack
Database : PostgreSQL
MessageBroker : Rabbit MQ
Database Migration : Liquibase

## Installation

make sure you already postgresql server ready and jdk 17 installed

### Database Migration

This project using liquibase for database migration, to change the database setting to migrated find the file named `liquibase.properties` under `src/main/resources`

Finally, go to root directory and run this command `./mvnw liquibase:update`

### Run the test and compile

To run the unit test and compile to runnable jar, run `mvn clean install`

Set the environment variable defined in `local.env`, or change the values here, then run `source local.env`

run the jar file using `java -jar target/*.jar`, the app will run in port 9090 by default, you can change it by give `-Dserver.port` parameter after `java -jar` command


### Application usage

The list of endpoint can be seen under `/swagger-ui/index.html`
