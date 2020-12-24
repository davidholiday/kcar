package io.holitek.kcar.routes;


import io.holitek.kcar.elements.HealthCheckBean;
import org.apache.camel.BeanScope;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;


/**
 * handler for requests to REST endpoint /healthcheck.
 */
public class HealthCheckRoute extends RouteBuilder {

    // the name by which the route will be identified in camel
    public static final String ROUTE_ID = HealthCheckRoute.class.getCanonicalName();

    // the key(s) expected to be in the application.properties file. when the service is started these will be used
    // to parse the application.properties file to create the necessary objects the route needs to function.
    //
    public static final String HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX = "healthCheckRoute";

    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT_PROPERTY_ID =
            HEALTH_CHECK_ROUTE_PROPERTY_ID_PREFIX + ".entryPoint";

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
                  /*
                  beans behave differently within a route. they appear to be created independently of whether or not
                  an instance of the bean class is already in the registry.
                   */
                  .log(LoggingLevel.INFO, "routing to -> bean: {{" + HEALTH_CHECK_BEAN_PROPERTY_ID + "}}")
                  .bean("{{" + HEALTH_CHECK_BEAN_PROPERTY_ID + "}}")
                  .log("${body}")
                  .bean("{{" + HEALTH_CHECK_PROCESSOR_PROPERTY_ID + "}}")
                  .to("mock:result");
    }

}