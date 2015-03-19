package org.lrodero.blueprintstests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;

public class TestLocal extends TestCommon {
    
    private static Logger logger = LoggerFactory.getLogger(TestLocal.class);
    private static final File LOCAL_GRAPHDB_DIR = new File("/tmp/graphdb");
    
    public static void main(String[] args) {
        new TestLocal().runTest();
    }
    
    @Override
    protected Graph createGraph() {
        
        if(LOCAL_GRAPHDB_DIR.exists()) {
            logger.info("Removing previous graph database folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(LOCAL_GRAPHDB_DIR);
            } catch (IOException e) { 
                logger.warn("Could not delete graph database folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath(), e);
            }
        }
        
        logger.info("Creating local graph database in folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath());
        return createLocalTitanGraph(LOCAL_GRAPHDB_DIR);
        // return createLocalOrientDBGraph(LOCAL_GRAPHDB_DIR);
        // return createLocalNeo4jGraph(LOCAL_GRAPHDB_DIR);
    }
    
    
    // LOCAL ACCESS TO TITAN //
    // Access to a graph database through Titan libraries, which will use berkeleydb 
    // to store data. It does not need a Titan server instance running.
    protected static Graph createLocalTitanGraph(File storageDir) {
        logger.info("Creating local Titan database configuration");
        Configuration conf = new BaseConfiguration();
        conf.setProperty("storage.directory", storageDir.getAbsolutePath());
        conf.setProperty("storage.backend", "berkeleyje");
        logger.info("Creating local Titan database instance");
        return TitanFactory.open(conf);
    }
    
    
    // LOCAL ACCESS TO NEO4J //
    // Access to a graph database through Neo4J libraries. It does not need
    // a Neo4J server instance running.
    protected static Graph createLocalNeo4jGraph(File storageDir) {
        logger.info("Creating local Neo4J database configuration");
        Configuration conf = new BaseConfiguration();
        conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph");
        conf.setProperty("blueprints.neo4j.directory", storageDir.getAbsolutePath());
        logger.info("Creating local Neo4J database instance");
        return GraphFactory.open(conf);
    }

    
    // LOCAL ACCESS TO ORIENTDB //
    // Access to a graph database through OrientDB libraries. It does not need
    // a OrientDB server instance running.
    protected static Graph createLocalOrientDBGraph(File storageDir) {
        logger.info("Creating local OrientDB database configuration");
        Configuration conf = new BaseConfiguration();
        conf.setProperty("blueprints.graph", "com.tinkerpop.blueprints.impls.orient.OrientGraph");
        conf.setProperty("blueprints.orientdb.url", "local:" + storageDir.getAbsolutePath());
        logger.info("Creating local OrientDB database instance");
        return GraphFactory.open(conf);
    }
    
}
