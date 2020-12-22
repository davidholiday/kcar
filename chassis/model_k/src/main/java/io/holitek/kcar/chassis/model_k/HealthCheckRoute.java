package io.holitek.kcar.chassis.model_k;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


/**
 *
 */
public class HealthCheckRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
                .get()
                .route()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                .setHeader(Exchange.CONTENT_TYPE, simple("application/json"))
                .bean(App.HEALTH_CHECK_BEAN_ID);

    }

}
