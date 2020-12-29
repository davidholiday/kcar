package io.holitek.kcar.routes;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;


/**
 * handler for requests to REST endpoint /healthcheck
 */
public class HealthCheckRoute extends RouteBuilder {

    // the name by which the route will be identified in camel
    public static final String ROUTE_ID = HealthCheckRoute.class.getCanonicalName();

    // the key(s) expected to be in the application.properties file. when the service is started these will be used
    // to parse the application.properties file to create the necessary objects the route needs to function.
    //
    // TODO there's got to be a way to load this into a map by master key where the results are <sub_key, value>
    public static final String HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX = "healthCheckRoute";

    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_ID =
            HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX + ".entryPoint";

    public static final String HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_ID =
            HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX + ".exitPoint";

    public static final String HEALTH_CHECK_BEAN_PROPERTY_ID =
            HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX + ".healthCheckBean";

    public static final String HEALTH_CHECK_PROCESSOR_PROPERTY_ID =
            HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX + ".healthCheckProcessor";


    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .scheme("https")
                           .dataFormatProperty("prettyPrint", "true");

        from("{{" + HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_ID + "}}")
                  .routeId(ROUTE_ID)
                  .log(LoggingLevel.INFO, "servicing /healthcheck request from: ${header.host}" )
                  .bean("{{" + HEALTH_CHECK_BEAN_PROPERTY_ID + "}}")
                  .bean("{{" + HEALTH_CHECK_PROCESSOR_PROPERTY_ID + "}}")
                  .to("{{" + HEALTH_CHECK_ROUTE_EXITPOINT_PROPERTY_ID + "}}");
    }

}