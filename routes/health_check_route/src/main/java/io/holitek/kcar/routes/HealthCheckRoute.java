package io.holitek.kcar.routes;


import static io.holitek.kcar.elements.HealthCheckBean.CAMEL_REGISTRY_ID;

import org.apache.camel.builder.RouteBuilder;


/**
 *
 */
public class HealthCheckRoute extends RouteBuilder {

    public static final String HEALTH_CHECK_BEAN_PROPERTY_ID = "healthCheckRoute.healthCheckBean";
    public static final String HEALTH_CHECK_PROCESSOR_PROPERTY_ID = "healthCheckRoute.healthCheckProcessor";

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
                .get()
                .route()
                .to("bean: {{" + HEALTH_CHECK_BEAN_PROPERTY_ID + "}}")
                .to("bean: {{" + HEALTH_CHECK_PROCESSOR_PROPERTY_ID + "}}");
    }

}