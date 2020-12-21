package io.holitek.kcar.chassis.model_k;


import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


public class App implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static final String HEALTH_CHECK_BEAN_ID = "healthCheckBean";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started... ");

            // create a new CamelContext
            CamelContext camelContext = new DefaultCamelContext();

            // configure where to load properties file in the properties component
            camelContext.getPropertiesComponent().setLocation("classpath:application.properties");

            // and create bean with the placeholder
            HealthCheckBean healthCheckBean = new HealthCheckBean();
            // register bean to Camel
            camelContext.getRegistry().bind(HEALTH_CHECK_BEAN_ID, healthCheckBean);

            // add routes to Camel
            camelContext.addRoutes(new HealthCheckRoute());

            // start Camel
            camelContext.start();

        } catch (Exception e) {
            LOG.error("something went wrong during context initialization", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("ServletContextListener destroyed... ");
    }

}