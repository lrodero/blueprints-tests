package org.lrodero.blueprintstests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public abstract class TestCommon {

    private static Logger logger = LoggerFactory.getLogger(TestCommon.class);

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
