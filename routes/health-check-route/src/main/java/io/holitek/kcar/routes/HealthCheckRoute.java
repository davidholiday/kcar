package io.holitek.kcar.routes;


import io.holitek.kcar.helpers.CamelPropertyHelper;

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

    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");

    public static final String HEALTH_CHECK_BEAN =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "healthCheckBean");

    public static final String HEALTH_CHECK_PROCESSOR =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "healthCheckProcessor");

    public static final String HEALTH_CHECK_ROUTE_EXITPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "exitPoint");

    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .scheme("https")
                           .dataFormatProperty("prettyPrint", "true");

        from(HEALTH_CHECK_ROUTE_ENTRYPOINT)
                  .routeId(NAMESPACE_KEY)
                  .log(LoggingLevel.INFO, "servicing /healthcheck request from: ${header.host}" )
                  .bean(HEALTH_CHECK_BEAN)
                  .bean(HEALTH_CHECK_PROCESSOR)
                  .to(HEALTH_CHECK_ROUTE_EXITPOINT);
    }

}
