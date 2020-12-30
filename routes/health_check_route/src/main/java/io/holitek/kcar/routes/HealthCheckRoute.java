package io.holitek.kcar.routes;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * handler for requests to REST endpoint /healthcheck
 */
public class HealthCheckRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckRoute.class);

    // anything to do with this route - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(HealthCheckRoute.class.getSimpleName());

    // TODO as we create more routes it'll probably make sense to make a helper utility common to all routes that
    // TODO     can take a do this boiler plate string building and keep the code DRY
    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_PLACEHOLDER =
            "{{" + NAMESPACE_KEY + ".entryPoint}}";

    public static final String HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_PLACEHOLDER =
            "{{" + NAMESPACE_KEY + ".exitPoint}}";

    public static final String HEALTH_CHECK_BEAN_PROPERTY_PLACEHOLDER =
            "{{" + NAMESPACE_KEY + ".healthCheckBean}}";

    public static final String HEALTH_CHECK_PROCESSOR_PROPERTY_PLACEHOLDER =
            "{{" + NAMESPACE_KEY + ".healthCheckProcessor}}";


    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .scheme("https")
                           .dataFormatProperty("prettyPrint", "true");

        from(HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_PLACEHOLDER)
                  .routeId(NAMESPACE_KEY)
                  .log(LoggingLevel.INFO, "servicing /healthcheck request from: ${header.host}" )
                  .bean(HEALTH_CHECK_BEAN_PROPERTY_PLACEHOLDER)
                  .log("${body}")
                  .bean(HEALTH_CHECK_PROCESSOR_PROPERTY_PLACEHOLDER)
                  .to(HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_PLACEHOLDER);
    }

}