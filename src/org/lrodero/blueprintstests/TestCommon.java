package org.lrodero.blueprintstests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public abstract class TestCommon {

    // Just logging configuration... in fact
    // this should be done through a configuration file in the classpath, not
    // programmatically, yet this way is faster and simpler :P
    static {configureLog4J();}
    private static Logger logger = LoggerFactory.getLogger(TestCommon.class);
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

    // To be implemented by child classes
    protected abstract Graph createGraph(); 
    
    // The test itself! It only uses blueprints APIs
    public void runTest() {
        
        Graph g = createGraph();
        
        logger.info("Adding vertices and edges following example at https://github.com/thinkaurelius/titan/wiki/Blueprints-Interface");
        Vertex juno = g.addVertex(null);
        juno.setProperty("name", "juno");
        Vertex jupiter = g.addVertex(null);
        jupiter.setProperty("name", "jupiter");
        Edge married = g.addEdge(null, juno, jupiter, "married");
        married.setProperty("at",2010);
        Vertex turnus = g.addVertex(null);
        turnus.setProperty("name", "turnus");
        Vertex hercules = g.addVertex(null);
        hercules.setProperty("name", "hercules");
        Vertex dido = g.addVertex(null);
        dido.setProperty("name", "dido");
        Vertex troy = g.addVertex(null);
        troy.setProperty("name", "troy");

        Edge edge = g.addEdge(null, juno, turnus, "knows");
        edge.setProperty("since",2010);
        edge.setProperty("stars",5);
        edge = g.addEdge(null, juno, hercules, "knows");
        edge.setProperty("since",2011);
        edge.setProperty("stars",1);
        edge = g.addEdge(null, juno, dido, "knows");
        edge.setProperty("since", 2011);
        edge.setProperty("stars", 5);
        g.addEdge(null, juno, troy, "likes").setProperty("stars",5);

        logger.info("Querying database for vertex '" + juno.getProperty("name") + "' neighbors");
        String neighbors = "";
        for(Vertex vertex : juno.query().vertices())
            neighbors += vertex.getProperty("name") + " ";
        logger.info(juno.getProperty("name") + " neighbors: " + neighbors);

        logger.info("Querying database for vertices that vertex '" + juno.getProperty("name") + "' knows since 2011 and have 5 stars");
        String known = "";
        Iterable<Vertex> results = juno.query().labels("knows").has("since",2011).has("stars",5).vertices();
        for(Vertex vertex : results)
            known += vertex.getProperty("name") + " ";
        logger.info(juno.getProperty("name") + " knows the following vertices since 2011 with 5 stars: " + known);
        
        logger.info("Shutting down graph database");
        g.shutdown();
    }
    
}
