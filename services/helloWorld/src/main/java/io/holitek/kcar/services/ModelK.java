package io.holitek.kcar.services;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * fires up camel in a servlet
 */
public class ModelK implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ModelK.class);

    public static final String NAMESPACE_KEY = "modelK";
    public static final String ROUTES_PROPERTY_KEY = "routes";


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started! Firing up Camel... ");
            CamelContext camelContext = new DefaultCamelContext();
            CamelPropertyHelper.loadPropertiesAndInjectRoutes(camelContext, NAMESPACE_KEY, ROUTES_PROPERTY_KEY);
            camelContext.start();
            LOG.info("*!* Camel is up! *!*");
        } catch (Exception e) {
            LOG.error("something went wrong during context initialization!", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("ServletContextListener destroyed... ");
    }

}