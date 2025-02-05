# tradestore 

Trade store processes trades in sequence. Trade has a trade id and version which togerther form primary key. Trades with lower version than what is already processed are discarded.  

*Note this implementation allows new higher versions to to stored in the store as separate row. One trade can have multiple versions in the DB.
If requirement was to only ever store the highest version in db a simpler implementation relying in db unique constraint was possible.*




##Uses the following
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











