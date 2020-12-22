package io.holitek.kcar.chassis.model_k.routes;


import io.holitek.kcar.chassis.model_k.App;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static io.holitek.kcar.chassis.model_k.common.HttpStrings.JSON_MEDIA_TYPE;
import static io.holitek.kcar.chassis.model_k.common.HttpStrings.HTTP_OK;


/**
 * HealthCheck route that always returns http200 OK and relies on a Bean that doesn't "Processor" to handle sending
 * the correct response status message.
 */
public class HealthCheckRouteWithHeaders extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
                .get()
                .route()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple(HTTP_OK))
                .setHeader(Exchange.CONTENT_TYPE, simple(JSON_MEDIA_TYPE))
                .bean(App.HEALTH_CHECK_HANDLER_ID);
    }

}
