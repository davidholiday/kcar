package io.holitek.kcar.chassis.model_k;


import io.holitek.kcar.chassis.model_k.routes.HealthCheckRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class App implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final String HEALTH_CHECK_HANDLER_ID = "healthCheckHandler";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started! Firing up Camel... ");
            //
            CamelContext camelContext = new DefaultCamelContext();
            camelContext.getPropertiesComponent().setLocation("classpath:application.properties");
            LOG.info(camelContext.getRegistry().lookupByName(HEALTH_CHECK_HANDLER_ID).toString());
            // register components(s)
            //
            //camelContext.getRegistry().bind(HEALTH_CHECK_BEAN_ID, HealthCheckProcessor.class);

            // register routes(s)
            //
            camelContext.addRoutes(new HealthCheckRoute());

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