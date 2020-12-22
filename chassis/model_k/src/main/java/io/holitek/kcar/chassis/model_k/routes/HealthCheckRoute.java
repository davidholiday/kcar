package io.holitek.kcar.chassis.model_k.routes;

import io.holitek.kcar.chassis.model_k.App;
import org.apache.camel.builder.RouteBuilder;


/**
 * relies on a 'Processor' to handle the entire healthcheck request. HTTP response code from this endpoint may vary on
 * fault state - unlike 'HealthCheckRouteWithBean'
 */
public class HealthCheckRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
                .get()
                .route()
                .to(App.HEALTH_CHECK_HANDLER_ID);
    }

}
