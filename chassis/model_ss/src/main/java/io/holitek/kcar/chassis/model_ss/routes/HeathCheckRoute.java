package io.holitek.kcar.chassis.model_ss.routes;


import io.holitek.kcar.chassis.model_ss.App;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


/**
 *
 */
public class HeathCheckRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .dataFormatProperty("prettyPrint", "true");

        rest().path("/healthcheck")
              .get()
              .route()
              .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
              .setHeader(Exchange.CONTENT_TYPE, simple("application/json"))
              .to("bean:" + App.HEALTH_CHECK_BEAN_ID);

    }

}
