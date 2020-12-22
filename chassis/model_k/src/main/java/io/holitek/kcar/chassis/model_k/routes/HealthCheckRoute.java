package io.holitek.kcar.chassis.model_k.routes;

import io.holitek.kcar.chassis.model_k.App;
import org.apache.camel.builder.RouteBuilder;


/**
 * relies on a 'Processor' to handle the entire healthcheck request. HTTP response code from this endpoint may vary on
 * fault state - unlike 'HealthCheckRouteWithBean'
 */
public class HealthCheckRoute extends RouteBuilder {

    // in order to be decoupled from the rest of the application, the route needs to define the handle it's looking for
    // when directing data to someplace else
    public static final String HEALTH_CHECK_HANDLER_ID = "healthCheckHandler";

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
                .get()
                .route()
                .to("bean:" + HEALTH_CHECK_HANDLER_ID);
    }

}
