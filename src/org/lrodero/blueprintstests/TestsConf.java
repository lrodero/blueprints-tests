package org.lrodero.blueprintstests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestsConf {

    private static Logger logger = LoggerFactory.getLogger(TestsConf.class);
    
    protected static final File CONFIGURATION_FILE = new File("tests.properties");
    
    private static TestsConf _singleton = null;
    private Properties conf = null;
    
    private TestsConf() {}
    
    private static TestsConf singleton() {
        if(_singleton == null) {
            _singleton = new TestsConf();
        }
        return _singleton;
    }
    
    public static void loadConfiguration() throws ConfigurationException {
        logger.info("Reading configuration from %s file", CONFIGURATION_FILE.getAbsolutePath());
        singleton().conf = new Properties();
        try {
            singleton().conf.load(new FileInputStream(CONFIGURATION_FILE));
        } catch (IOException e) {
            logger.error("Could not load configuration file", e);
            throw new ConfigurationException();
        }
    }
    
    public static String readParameter(String parameterName) {
        String value = singleton().conf.getProperty(parameterName);
        if(value == null)
            logger.error("'%s' configuration parameter not set", parameterName);
        return value;
    }

}
