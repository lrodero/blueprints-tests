/*
 * Copyright Luis Rodero-Merino 2015
 * 
 * APACHE LICENSE v2.0
 * 
 * Author: Dr. Luis Rodero-Merino (lrodero@acm.org)
 */
package org.lrodero.blueprintstests;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    // Just logging configuration... in fact
    // this should be done through a configuration file in the classpath, not
    // programmatically, yet this way is faster and simpler for the user (although 
    // not quite flexible :P )
    static {configureLog4J();}
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static void configureLog4J() {
        
        // Resetting log4j configuration
        org.apache.log4j.LogManager.resetConfiguration();
        org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
        
        // Setting log for this package
        org.apache.log4j.Logger pckLogger = org.apache.log4j.Logger.getLogger("org.lrodero.blueprintstests");
        pckLogger.setLevel(org.apache.log4j.Level.ALL);
        org.apache.log4j.ConsoleAppender console = new org.apache.log4j.ConsoleAppender();
        console.setLayout(new org.apache.log4j.PatternLayout("%-5p [%t] %C{1}: %m%n")); 
        console.activateOptions();
        pckLogger.addAppender(console);
        
        // Setting log for titan sw
        org.apache.log4j.Logger titanLogger = org.apache.log4j.Logger.getLogger("com.thinkaurelius.titan");
        titanLogger.setLevel(org.apache.log4j.Level.WARN);
        org.apache.log4j.ConsoleAppender titanConsole = new org.apache.log4j.ConsoleAppender();
        titanConsole.setLayout(new org.apache.log4j.PatternLayout("TITAN > %-5p [%t] %C{1}: %m%n")); 
        titanConsole.activateOptions();
        titanLogger.addAppender(titanConsole);
        
        // Setting log for thinkerpop (blueprints) sw
        org.apache.log4j.Logger thinkerpopLogger = org.apache.log4j.Logger.getLogger("com.tinkerpop");
        thinkerpopLogger.setLevel(org.apache.log4j.Level.WARN);
        org.apache.log4j.ConsoleAppender thinkerpopConsole = new org.apache.log4j.ConsoleAppender();
        thinkerpopConsole.setLayout(new org.apache.log4j.PatternLayout("THINKERPOP > %-5p [%t] %C{1}: %m%n"));
        thinkerpopConsole.activateOptions();
        thinkerpopLogger.addAppender(thinkerpopConsole);
        
        // Setting log for Neo4J sw
        org.apache.log4j.Logger neo4jLogger = org.apache.log4j.Logger.getLogger("org.neo4j");
        neo4jLogger.setLevel(org.apache.log4j.Level.WARN);
        org.apache.log4j.ConsoleAppender neo4jConsole = new org.apache.log4j.ConsoleAppender();
        neo4jConsole.setLayout(new org.apache.log4j.PatternLayout("NEO4J > %-5p [%t] %C{1}: %m%n"));
        neo4jConsole.activateOptions();
        neo4jLogger.addAppender(neo4jConsole);
        
        // Setting log for OrientDB sw
        org.apache.log4j.Logger orientdbLogger = org.apache.log4j.Logger.getLogger("com.orienttechnologies");
        orientdbLogger.setLevel(org.apache.log4j.Level.WARN);
        org.apache.log4j.ConsoleAppender orientdbConsole = new org.apache.log4j.ConsoleAppender();
        orientdbConsole.setLayout(new org.apache.log4j.PatternLayout("ORIENTDB > %-5p [%t] %C{1}: %m%n"));
        orientdbConsole.activateOptions();
        orientdbLogger.addAppender(orientdbConsole);
        
        // Setting log for Astyanax (software from Netflix used by Titan's Cassandra client
        org.apache.log4j.Logger astyanaxLogger = org.apache.log4j.Logger.getLogger("com.netflix.astyanax");
        astyanaxLogger.setLevel(org.apache.log4j.Level.WARN);
        org.apache.log4j.ConsoleAppender astyanaxConsole = new org.apache.log4j.ConsoleAppender();
        astyanaxConsole.setLayout(new org.apache.log4j.PatternLayout("ASTYANAX > %-5p [%t] %C{1}: %m%n"));
        astyanaxConsole.activateOptions();
        astyanaxLogger.addAppender(astyanaxConsole);
    }
    protected static final String TEST_TYPE_PARAMETER_NAME = "Test";
    protected static enum TEST_TYPE {LOCAL, REMOTE};
    
    public static void main(String[] args) {
        new Main().run();
    }
    
    protected void run() {
        
        try {
            TestsConf.loadConfiguration();
        } catch (ConfigurationException e) {
            logger.error("Could not load configuration, aborting");
            return;
        }
        
        TEST_TYPE testType = readTestTypeConf();
        if(testType == null) {
            logger.error("Could not read test type from configuration, aborting");
            return;
        }
        
        if(testType == TEST_TYPE.LOCAL) {
            logger.info("Running local test");
            new TestLocal().runTest();
        } else {
            logger.info("Running distributed test");
            new TestDistributed().runTest();
        }
        
        logger.info("Test terminated");
        
        System.exit(0); // Aggressive, but required to terminate some pending threads... 
    }
    
    protected static TEST_TYPE readTestTypeConf() {
        String testTypeParam = TestsConf.readParameter(TEST_TYPE_PARAMETER_NAME);
        if(testTypeParam == null)
            return null;
        
        TEST_TYPE testType = null;
        try {
            testType = TEST_TYPE.valueOf(testTypeParam.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("'%s' property has not a valid value '%s', valid ones are: %s",
                    TEST_TYPE_PARAMETER_NAME, testTypeParam, java.util.Arrays.toString(TEST_TYPE.values()));
        }
        
        return testType;
    }

}
