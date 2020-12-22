package io.holitek.kcar.chassis.model_k.routes;


import io.holitek.kcar.chassis.model_k.App;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


/*
TODO get rid of common HTTPStrings class. the io.holitek.kcar.routes need to be wholly self contained, as does the service chassis!
*/
import static io.holitek.kcar.chassis.model_k.common.HttpStrings.JSON_MEDIA_TYPE;
import static io.holitek.kcar.chassis.model_k.common.HttpStrings.HTTP_OK;


/**
 * HealthCheck route that always returns http200 OK and relies on a Bean that doesn't "Processor" to handle sending
 * the correct response status message.
 */
public class HealthCheckRouteWithHeaders extends RouteBuilder {

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
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple(HTTP_OK))
                .setHeader(Exchange.CONTENT_TYPE, simple(JSON_MEDIA_TYPE))
                .bean("bean:" + HEALTH_CHECK_HANDLER_ID);
    }

}
