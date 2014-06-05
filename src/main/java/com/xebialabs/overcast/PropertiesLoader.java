package com.xebialabs.overcast;

import java.io.File;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PropertiesLoader {

    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    public static final String OVERCAST_CONF_FILE = "overcast.conf";
    public static final String OVERCAST_USER_DIR = ".overcast";

    private static String getUserOvercastConfPath() {
        return new File(new File(System.getProperty("user.home"), OVERCAST_USER_DIR), OVERCAST_CONF_FILE).getAbsolutePath();
    }

    public static Config loadOvercastConfig() {
        return loadOvercastConfigFromFile(getUserOvercastConfPath())
                .withFallback(loadOvercastConfigFromFile(OVERCAST_CONF_FILE))
                .withFallback(loadOvercastConfigFromClasspath(OVERCAST_CONF_FILE))
                .withFallback(ConfigFactory.systemProperties())
                .resolve();
    }

    /** Load {@link Config} from 'file' but do not resolve it. */
    static Config loadOvercastConfigFromClasspath(String path) {
        try {
            // resource.toURI().getPath() so path gets URL decoded so spaces are no issue
            // using resource.getPath() runs into a bug with guava
            return loadOvercastConfigFromFile(Resources.getResource(path).toURI().getPath());
        } catch (IllegalArgumentException e) {
            logger.warn("File '{}' not found on classpath.", path);
        } catch (URISyntaxException e) {
            logger.warn("File '{}' not found on classpath.", path);
        }
        return ConfigFactory.empty();
    }

    /** Load {@link Config} from 'file' but do not resolve it. */
    static Config loadOvercastConfigFromFile(String file) {
        File f = new File(file);
        if (!f.exists()) {
            logger.warn("File {} not found.", f.getAbsolutePath());
            return ConfigFactory.empty();
        }

        logger.info("Loading from file {}", f.getAbsolutePath());
        return ConfigFactory.parseFile(f);
    }
}
