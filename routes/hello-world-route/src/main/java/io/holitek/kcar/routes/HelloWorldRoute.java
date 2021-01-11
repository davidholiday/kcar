package io.holitek.kcar.routes;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * sample route that exposes a REST endpoint. depending on properties loaded from properties file, channels the
 * request to business logic handlers before returning results to caller
 */
public class HelloWorldRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldRoute.class);

    // anything to do with this route - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(HelloWorldRoute.class.getSimpleName());

    public static final String ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");

    public static final String ROUTE_EXITPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "exitPoint");

    public static final String HELLO_WORLD_BEAN =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "helloWorldBean");

    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .scheme("https")
                           .dataFormatProperty("prettyPrint", "true");

        from(ROUTE_ENTRYPOINT)
                  .routeId(NAMESPACE_KEY)
                  .log(LoggingLevel.INFO, "servicing "+ ROUTE_ENTRYPOINT + " request from: ${header.host}")
                  .to(HELLO_WORLD_BEAN)
                  .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
                  .to(ROUTE_EXITPOINT);
    }

}
