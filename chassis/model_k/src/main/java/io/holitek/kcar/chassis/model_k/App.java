package io.holitek.kcar.chassis.model_k;


import io.holitek.kcar.chassis.model_k.routes.HealthCheckRoute;

import io.holitek.kcar.chassis.model_k.routes.HealthCheckRouteWithHeaders;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class App implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            LOG.info("ServletContextListener started! Firing up Camel... ");
            //
            CamelContext camelContext = new DefaultCamelContext();
            camelContext.getPropertiesComponent().setLocation("classpath:application.properties");

            //camelContext.getPropertiesComponent().getLocalProperties().
            // register components(s)
            // TODO push the config loading (based on env var indicating runtime context) and registry populating to
            //      at least its own method if not class
            String healthcheckProcessorPropertyPlaceholder = "{{" + HealthCheckRoute.HEALTH_CHECK_HANDLER_ID + "}}";
            String className = camelContext.resolvePropertyPlaceholders((healthcheckProcessorPropertyPlaceholder));
            camelContext.getRegistry().bind(HealthCheckRoute.HEALTH_CHECK_HANDLER_ID, Class.forName(className));

            // register routes(s)
            //
            camelContext.addRoutes(new HealthCheckRouteWithHeaders());

            //
            camelContext.start();
//            Main camelMain = new Main();
//            camelMain.configure().addRoutesBuilder(new HealthCheckRoute());
//            camelMain.run();
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