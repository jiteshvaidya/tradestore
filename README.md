# tradestore using

### Kafka for Messaging
### Postgresql as relational data store
### Mongodb as the no sql data store
### Quartz for scheduling jobs
### Liquibase for tracking db changes





## Automated testing
Unit test and integration test case  

For unit and integration testing the project uses
### Embeded H2 as rdbms
### Embded kafka
### mongo instance crated through ci cd before build

## Local setup
### Download and install kafka

Start zookeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

Start kafka server
.\bin\windows\kafka-server-start.bat .\config\server.properties

### Download and install Mongo Db
Download installable for windows from https://www.mongodb.com/try/download/community

Create folder C:\data\db

Start Mongodb 
c:\Program Files\MongoDB\Server\8.0\bin>mongod.exe

### Start a RDBMS
Here we are using postgresql  











