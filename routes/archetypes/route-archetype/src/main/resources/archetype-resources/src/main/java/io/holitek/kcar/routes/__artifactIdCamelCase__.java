#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId};


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * sample route that exposes a REST endpoint. depending on properties loaded from properties file, channels the
 * request to business logic handlers before returning results to caller
 */
public class ${artifactIdCamelCase} extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(${artifactIdCamelCase}.class);

    // anything to do with this route - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(${artifactIdCamelCase}.class.getSimpleName());

    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");

    public static final String HEALTH_CHECK_BEAN =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "healthCheckBean");

    public static final String HEALTH_CHECK_PROCESSOR =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "healthCheckProcessor");

    public static final String HEALTH_CHECK_ROUTE_EXITPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "exitPoint");

    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet")
                           .scheme("https")
                           .dataFormatProperty("prettyPrint", "true");

        from(HEALTH_CHECK_ROUTE_ENTRYPOINT)
                  .routeId(NAMESPACE_KEY)
                  .log(LoggingLevel.INFO, "servicing /healthcheck request from: ${symbol_dollar}{header.host}" )
                  .bean(HEALTH_CHECK_BEAN)
                  .bean(HEALTH_CHECK_PROCESSOR)
                  .to(HEALTH_CHECK_ROUTE_EXITPOINT);
    }

}
