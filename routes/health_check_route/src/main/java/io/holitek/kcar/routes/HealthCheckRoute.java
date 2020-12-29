package io.holitek.kcar.routes;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;


/**
 * handler for requests to REST endpoint /healthcheck
 */
public class HealthCheckRoute extends RouteBuilder {

    // the name by which the route will be identified in camel
    public static final String ROUTE_ID = HealthCheckRoute.class.getCanonicalName();

    // the key(s) expected to be in the healthCheckRoute.application.properties file. when the service is started these will be used
    // to parse the appropriate properties file to create the necessary objects the route needs to function.
    public static final String PROPERTIES_NAMESPACE_KEY = "healthCheckRoute";


    // TODO as we create more routes it'll probably make sense to make a helper utility common to all routes that
    // TODO     can take a do this boiler plate string building and keep the code DRY
    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_PLACEHOLDER =
            "{{" + PROPERTIES_NAMESPACE_KEY + ".entryPoint}}";

    public static final String HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_PLACEHOLDER =
            "{{" + PROPERTIES_NAMESPACE_KEY + ".exitPoint}}";

    public static final String HEALTH_CHECK_BEAN_PROPERTY_PLACEHOLDER =
            "{{" + PROPERTIES_NAMESPACE_KEY + ".healthCheckBean}}";

    public static final String HEALTH_CHECK_PROCESSOR_PROPERTY_PLACEHOLDER =
            "{{" + PROPERTIES_NAMESPACE_KEY + ".healthCheckProcessor}}";


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
                  .routeId(ROUTE_ID)
                  .log(LoggingLevel.INFO, "servicing /healthcheck request from: ${header.host}" )
                  .bean(HEALTH_CHECK_BEAN_PROPERTY_PLACEHOLDER)
                  .bean(HEALTH_CHECK_PROCESSOR_PROPERTY_PLACEHOLDER)
                  .to(HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_PLACEHOLDER);
    }

}