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
public class GithubToJiraRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraRoute.class);

    // anything to do with this route - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(GithubToJiraRoute.class.getSimpleName());

    public static final String ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");

    public static final String ROUTE_EXITPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "exitPoint");

    public static final String GRAPH_QL_QUERY_TEMPLATE =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubGraphQlQuery");

    public static final String GITHUB_GRAPH_QL_API =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubGraphQlUri");

    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        from(ROUTE_ENTRYPOINT)
                  .routeId(NAMESPACE_KEY)
                  .log(LoggingLevel.INFO, "servicing "+ ROUTE_ENTRYPOINT + " request with body: ${body}")
                  .choice()
                    .when(header("afterCursor").isNull())
                      .setHeader("afterCursor", simple(""))
                  .end()
                  .log("header is ${headers}")
                  .setHeader("CamelVelocityTemplate").constant(GRAPH_QL_QUERY_TEMPLATE)
                  .to("velocity:dummy?allowTemplateFromHeader=true")
                  .log(LoggingLevel.DEBUG, "body is: ${body}")
                  .to(GITHUB_GRAPH_QL_API)
                  .to(ROUTE_EXITPOINT);
    }

}
