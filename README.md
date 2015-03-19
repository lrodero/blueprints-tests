# README #

This software is a small set of Java classes used to test three different graph database technologies (BerkeleyDB, OrientDB and Neo4J) as a backend for Titan GraphDB in local and distributed settings. The goal of this software is to detemine the proper configurations required by Titan to be run on different settings, and to check that those configurations indeed work.

## Getting the code

The code uses JDK 7 and maven 3.0.3 for dependency management (and optionally execution). To get a copy of this code just run
```
$ git clone https://github.com/lrodero/blueprints-tests.git
```
in a Linux shell. Then, to compile the code issue the following command (again in a shell):
```
$ cd blueprints-tests
$ mvn compiler:compile
```

## Description

The tests to run are defined in the `src/org/lrodero/blueprintstests/TestCommon.java` file (`runTest()` method), they are pretty simple as the main goal of this software is just to test the feasibility of using different pluggable technologies as Titan's storage backend. Both local and remote technologies can be tested.

Just for conveniene the logs configuration is hardcoded in the main file `src/org/lrodero/blueprintstests/Main.java` file.

Configuration is kept in file `test.properties`.

## Testing Local Backend Technologies
To run a local test you must edit the `test.properties` and ensure that the `Test` parameter is set to `LOCAL`. Also, you must choose the backend technology to use for the test in the `Local.Backend` parameter, valid options are `BERKELEYDB`, `ORIENTDB` and `NEO4J`. Example:
```properties
Test = LOCAL
Local.Backend = BERKELEYDB
```

Then, execute the following command in the shell:
```
$ mvn exec:java -Dexec.mainClass="org.lrodero.blueprintstests.Main"
``` 

## Testing Remote Backend Technologies
To run a remote (distributed) test it is necessary to ensure that the remote backend technology is started, please read the subsections below to see how to start a [Cassandra backend](#cassandra) of a [OrientDB](#orientdb) backend.

Once the remote backend server is ready, then it is necessary to edit `test.properties` so the `Test` parameter is set to `REMOTE` and set the `Remote.Backend` property to the corresponding backend technology, valid options are `CASSANDRA` and `ORIENTDB`. Example:
```properties
Test = REMOTE
Remote.Backend = CASSANDRA
```

### <a name="cassandra"/>Setting and Starting Cassandra
For convenience we can use the cassandra distribution included in Titan. First we download the following and unzip the 0.5.2 version of Titan, then we unzip it and run Cassandra startup script:
```
$ wget http://s3.thinkaurelius.com/downloads/titan/titan-0.5.2-hadoop2.zip
$ unzip titan-0.5.2-hadoop2.zip
$ cd titan-0.5.2-hadoop2
$ ./bin/cassandra
```

### <a name="orientdb"/>"Setting OrientDB
First we need to download OrientDB, we have used the 1.7.10 version. Go to the [OrientDB download](http://www.orientechnologies.com/download/) webpage and download the version for Linux systems (we recommend version 1.7.10). Then run the following commands:
```
$ tar -zxvf orientdb-community-1.7.10.tar.gz
$ cd orientdb-community-1.7.10/bin
$ ./start.sh
```


