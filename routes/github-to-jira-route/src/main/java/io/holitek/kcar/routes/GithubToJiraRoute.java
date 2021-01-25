package io.holitek.kcar.routes;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.List;


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

    public static final String GITHUB_GRAPH_QL_URI =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubGraphQlUri");

    public static final String GITHUB_GRAPHQL_AFTER_CURSOR = "afterCursor";

    public static final String GITHUB_GRAPHQL_AFTER_CURSOR_TEMP = "afterCursorTemp";

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
                    .when(header(GITHUB_GRAPHQL_AFTER_CURSOR).isNull())
                      .setHeader(GITHUB_GRAPHQL_AFTER_CURSOR, simple(""))
                  .end()
                  .log(LoggingLevel.INFO, "header is ${headers}")
                  .setHeader("CamelVelocityTemplate").constant(GRAPH_QL_QUERY_TEMPLATE)
                  .to("velocity:dummy?allowTemplateFromHeader=true")
                  .log(LoggingLevel.INFO,
                          "graphQL URI is: "
                                  + GITHUB_GRAPH_QL_URI
                                  + "query=${body}&accessToken=${env.GITHUB_ACCESS_TOKEN}"
                  )
                  .toD(GITHUB_GRAPH_QL_URI + "query=${body}&accessToken=${env.GITHUB_ACCESS_TOKEN}")
                  .log(LoggingLevel.INFO, "response is: ${body}")
                  .choice()
                    .when().jsonpath("$.data.viewer.organization.repositories.pageInfo.[?(@.hasNextPage == true)]")
                      .setHeader(GITHUB_GRAPHQL_AFTER_CURSOR_TEMP)
                        .jsonpath("$.data.viewer.organization.repositories.pageInfo.endCursor")
                      .setHeader(GITHUB_GRAPHQL_AFTER_CURSOR, simple("after:\"${headers." + GITHUB_GRAPHQL_AFTER_CURSOR_TEMP + "}\""))
                      .to(ROUTE_ENTRYPOINT)
                    .otherwise()
                      .to(ROUTE_EXITPOINT)
                  .end();
    }

}
