package io.holitek.kcar.chassis.model_k;


import io.holitek.kcar.helpers.CamelPropertyHelper;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * fires up camel in a servlet
 */
public class App implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final String NAMESPACE_KEY = "modelK";
    public static final String ROUTES_PROPERTY_KEY = "routes";


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started! Firing up Camel... ");

            //
            CamelContext camelContext = new DefaultCamelContext();



            //
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