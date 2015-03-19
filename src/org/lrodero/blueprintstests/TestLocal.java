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
    
    protected static enum LOCAL_BACKEND_TECHNOLOGY_TYPE {BERKELEYDB, ORIENTDB, NEO4J};
    protected static final String LOCAL_BACKEND_TECHNOLOGY_PARAMETER_NAME = "Local.Backend";
    
    @Override
    protected Graph createGraph() {
        
        LOCAL_BACKEND_TECHNOLOGY_TYPE backEndTech = readBackEndTechConf();
        if(backEndTech == null) {
            logger.error("Could not read back end technology from configuration, aborting");
            return null;
        }
        
        if(LOCAL_GRAPHDB_DIR.exists()) {
            logger.info("Removing previous graph database folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(LOCAL_GRAPHDB_DIR);
            } catch (IOException e) { 
                logger.warn("Could not delete graph database folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath(), e);
            }
        }
        
        logger.info("Creating local graph database in folder " + LOCAL_GRAPHDB_DIR.getAbsolutePath());
        
        if(backEndTech == LOCAL_BACKEND_TECHNOLOGY_TYPE.BERKELEYDB) {
            logger.info("Using BerkeleyDB as local backend technology");
            return createLocalTitanGraph(LOCAL_GRAPHDB_DIR);
        } else if(backEndTech == LOCAL_BACKEND_TECHNOLOGY_TYPE.ORIENTDB) {
            logger.info("Using OrientDB as local backend technology");
            return createLocalOrientDBGraph(LOCAL_GRAPHDB_DIR);
        } else {
            logger.info("Using Neo4j as local backend technology");
            return createLocalNeo4jGraph(LOCAL_GRAPHDB_DIR);
        }
    }
    
    protected static LOCAL_BACKEND_TECHNOLOGY_TYPE readBackEndTechConf() {

        String backEndTechParam = TestsConf.readParameter(LOCAL_BACKEND_TECHNOLOGY_PARAMETER_NAME);
        if(backEndTechParam == null)
            return null;
        
        LOCAL_BACKEND_TECHNOLOGY_TYPE backEndTech = null;
        try {
            backEndTech = LOCAL_BACKEND_TECHNOLOGY_TYPE.valueOf(backEndTechParam.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("'%s' property has not a valid value '%s', valid ones are: %s",
                    LOCAL_BACKEND_TECHNOLOGY_PARAMETER_NAME, backEndTechParam, java.util.Arrays.toString(LOCAL_BACKEND_TECHNOLOGY_TYPE.values()));
        }
        
        return backEndTech;
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
