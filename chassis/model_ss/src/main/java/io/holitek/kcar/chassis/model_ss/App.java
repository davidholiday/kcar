package io.holitek.kcar.chassis.model_ss;


import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import io.holitek.kcar.chassis.model_ss.components.HealthCheckBean;
import io.holitek.kcar.chassis.model_ss.io.holitek.kcar.routes.HeathCheckRoute;


/**
 * bootstraps the app. taken from https://preview.tinyurl.com/y7re29wt
 */
public class App {

    private App() {}

    public static final String HEALTH_CHECK_BEAN_ID = "healthCheckBean";

    public static void main(String[] args) throws Exception {
        // create a new CamelContext
        CamelContext camelContext = new DefaultCamelContext();

        // configure where to load properties file in the properties component
        camelContext.getPropertiesComponent().setLocation("classpath:application.properties");

        // and create bean with the placeholder
        HealthCheckBean healthCheckBean = new HealthCheckBean();
        // register bean to Camel
        camelContext.getRegistry().bind(HEALTH_CHECK_BEAN_ID, healthCheckBean);

        // add io.holitek.kcar.routes to Camel
        camelContext.addRoutes(new HeathCheckRoute());

        // start Camel
        camelContext.start();
    }

}
