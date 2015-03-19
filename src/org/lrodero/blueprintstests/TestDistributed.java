package org.lrodero.blueprintstests;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;

public class TestDistributed extends TestCommon {
    
    private static Logger logger = LoggerFactory.getLogger(TestDistributed.class);

    protected static enum REMOTE_BACKEND_TECHNOLOGY_TYPE {CASSANDRA, ORIENTDB};
    protected static final String REMOTE_BACKEND_TECHNOLOGY_PARAMETER_NAME = "Remote.Backend";
    
    @Override
    protected Graph createGraph() {
        REMOTE_BACKEND_TECHNOLOGY_TYPE backEndTech = readBackEndTechConf();
        if(backEndTech == null) {
            logger.error("Could not read back end technology from configuration, aborting");
            return null;
        }
            
        if(backEndTech == REMOTE_BACKEND_TECHNOLOGY_TYPE.CASSANDRA) {
            logger.info("Using Cassandra as remote backend technology");
            return createRemoteTitanCassandraGraph(DEFAULT_CASSANDRA_HOST, DEFAULT_CASSANDRA_PORT);
        } else {
            logger.info("Using OrientDB as remote backend technology");
            return createRemoteOrientDBGraph(DEFAULT_ORIENTDB_HOST, DEFAULT_ORIENTDB_PORT, DEFAULT_ORIENTDB_DBNAME, DEFAULT_ORIENTDB_USER, DEFAULT_ORIENTDB_PASSWORD);
        }
    }
    
    protected static REMOTE_BACKEND_TECHNOLOGY_TYPE readBackEndTechConf() {

        String backEndTechParam = TestsConf.readParameter(REMOTE_BACKEND_TECHNOLOGY_PARAMETER_NAME);
        if(backEndTechParam == null)
            return null;
        
        REMOTE_BACKEND_TECHNOLOGY_TYPE backEndTech = null;
        try {
            backEndTech = REMOTE_BACKEND_TECHNOLOGY_TYPE.valueOf(backEndTechParam.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("'%s' property has not a valid value '%s', valid ones are: %s",
                    REMOTE_BACKEND_TECHNOLOGY_PARAMETER_NAME, backEndTechParam, java.util.Arrays.toString(REMOTE_BACKEND_TECHNOLOGY_TYPE.values()));
        }
        
        return backEndTech;
    }
    
    
    // REMOTE ACCESS TO TITAN/CASSANDRA //
    // Access to a graph database in a remote location, kept by Cassandra. Titan will run in
    // this same JVM, we will just configure it to use it to use that Cassandra instance as database.
    // We could download and start Cassandra separately, but when using Titan it is easier just to use
    // the Cassandra sw they include in the Titan distribution which can be downloaded from
    // http://s3.thinkaurelius.com/downloads/titan/titan-0.5.2-hadoop2.zip
    // Once that .zip file is downloaded and uncompressed, it is enough to run 'bin/cassandra'
    // to start the database.
    protected static Graph createRemoteTitanCassandraGraph(String cassandraHostName, int cassandraPort) {
        logger.info("Creating remote Titan database configuration (using Cassandra for storage)");
        Configuration conf = new BaseConfiguration();
        conf.setProperty("storage.backend","cassandra");
        conf.setProperty("storage.hostname", cassandraHostName);
        conf.setProperty("storage.port", cassandraPort);
        logger.info("Creating remote Titan database instance (using Cassandra for storage)");
        return TitanFactory.open(conf);
    }
    // The default Cassandra configuration values assume that it has been started in the same
    // host this software is running on. The port is the one set in the default Cassandra configuration.
    // Unlike OrientDB, it does not need to connect to a specific database.
    protected static final String DEFAULT_CASSANDRA_HOST = "127.0.0.1"; // Assuming we run Cassandra in the localhost
    protected static final int DEFAULT_CASSANDRA_PORT = 9160; // The 'rpc_port' parameter in conf/cassandra.yaml
    
    
    // REMOTE ACCESS TO ORIENTDB //
    // Access to graph database in a remote location, kept by OrientDB. When using remote conns, for security,
    // OrientDB requires the database to have been created before connecting to it (in local ddbbs, the ddbb
    // is created if it does not exist). To create a new database you must start the OrientDB server (server.sh in bin),
    // then start the console (console.sh in bin) and in the console run this command:
    //   create database remote:localhost/test root 'root_pass' local
    // (the 'root_pass' is configured in config/orientdb-server-config.xml, look for <users> tag)
    protected static Graph createRemoteOrientDBGraph(String orientdbHostName, int orientdbPort, String dbName, String userName, String password) {
        logger.info("Creating remote OrientDB database configuration");
        Configuration conf = new BaseConfiguration();
        conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.orient.OrientGraph");
        conf.setProperty("blueprints.orientdb.url", "remote:" + orientdbHostName + ":" + orientdbPort + "/" + dbName);
        conf.setProperty("blueprints.orientdb.username", userName); // In fact we should use the 'guest' user for security, but anyway
        conf.setProperty("blueprints.orientdb.password", password); // Extracted from <users> tag in config/orientdb-server-config.xml)
        logger.info("Creating remote OrientDB database instance");
        return GraphFactory.open(conf);
    }
    // The default OrientDB configuration values assume that it has been started in the same
    // host this software is running on. The port is the one set in the default OrientDB configuration (see
    // first port in the range of ports for binary protocol, in <listeners> tag in config/orientdb-server-config.xml).
    // Also we assume that the database created was named 'test'.
    protected static final String DEFAULT_ORIENTDB_HOST = "127.0.0.1"; // Assuming we run OrientDB in the localhost
    protected static final int DEFAULT_ORIENTDB_PORT = 2424; 
    protected static final String DEFAULT_ORIENTDB_DBNAME = "test";  
    protected static final String DEFAULT_ORIENTDB_USER = "root";
    // This is the root password, which must be extracted from the config/orientdb-server-config.xml file, look for the <users> tag 
    protected static final String DEFAULT_ORIENTDB_PASSWORD = "20213E5B8C8850E2C7BD9C34FB727919EFC6AB3E29AD7741C07BB311A875ACE6";
    
    
    // REMOTE ACCESS TO NEO4J - NOT IMPLEMENTED! //
    // It looks like to be able to access to NEO4J remotely we need the HA version, which is not the community one (?), or
    // we will have to use the REST protocol NEO4J offers... in any case does not look good.
    // A possible solution would be to wrap NEO4J with a REXSTER instance, see
    // https://github.com/tinkerpop/rexster/wiki/Specific-Graph-Configurations.
    protected static Graph createRemoteNeo4JCassandraGraph(String neo4jHostName, int neo4jPort) {
        throw new UnsupportedOperationException("");
    }
    
}
