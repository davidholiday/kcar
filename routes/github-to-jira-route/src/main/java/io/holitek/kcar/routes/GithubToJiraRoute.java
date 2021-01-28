package io.holitek.kcar.routes;


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
public class GithubToJiraRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraRoute.class);

    // anything to do with this route - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY =
            Introspector.decapitalize(GithubToJiraRoute.class.getSimpleName());

    public static final String ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");

    public static final String ROUTE_EXITPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "exitPoint");

    public static final String GRAPH_QL_QUERY_TEMPLATE =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubGraphQlQuery");

    public static final String GITHUB_GRAPH_QL_URI =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubGraphQlUri");

    public static final String PAGINATED_RESPONSE_BEAN__ADD =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "paginatedResponseBeanAdd");

    public static final String GITHUB_TO_JIRA_TRANSFORMER =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "githubToJiraTransformer");

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
          .log(LoggingLevel.INFO, "servicing "+ ROUTE_ENTRYPOINT )
          // make sure the exchange header has the expected KVs
          .choice()
            .when(header(GITHUB_GRAPHQL_AFTER_CURSOR).isNull())
              .setHeader(GITHUB_GRAPHQL_AFTER_CURSOR, simple(""))
          .end()
          // create github graphql from template
          .log(LoggingLevel.DEBUG, "header is ${headers}")
          .setHeader("CamelVelocityTemplate").constant(GRAPH_QL_QUERY_TEMPLATE)
          .to("velocity:dummy?allowTemplateFromHeader=true")
          .log(LoggingLevel.DEBUG,
                  "graphQL URI is: "
                          + GITHUB_GRAPH_QL_URI
                          + "query=${body}&accessToken=${env.GITHUB_ACCESS_TOKEN}"
          )
          // send query
          .toD(GITHUB_GRAPH_QL_URI + "query=${body}&accessToken=${env.GITHUB_ACCESS_TOKEN}")
          .log(LoggingLevel.DEBUG, "response from github is: ${body}")
          // handle pagination
          .choice()
            .when().jsonpath("$.data.viewer.organization.repositories.pageInfo.[?(@.hasNextPage == true)]")
              // TODO is there a way to do this in one step instead of two?
              .setHeader(GITHUB_GRAPHQL_AFTER_CURSOR_TEMP)
                .jsonpath("$.data.viewer.organization.repositories.pageInfo.endCursor")
              .setHeader(
                      GITHUB_GRAPHQL_AFTER_CURSOR,
                      simple("after:\"${headers." + GITHUB_GRAPHQL_AFTER_CURSOR_TEMP + "}\"")
              )
              // TODO make this an aggregator
              .to(PAGINATED_RESPONSE_BEAN__ADD)
              .log("fetching next page after cursor: ${headers." + GITHUB_GRAPHQL_AFTER_CURSOR_TEMP + "}")
              .to(ROUTE_ENTRYPOINT)
          .end()
          // translate github payloads into jira payloads.
          .to("bean:io.holitek.kcar.elements.PaginatedResponseBean?method=getNumberOfPaginatedResponses")
          .log("number of pages is: ${body}")
          .loop(body())
            .to("bean:io.holitek.kcar.elements.PaginatedResponseBean?method=popPaginatedResponse")
            .to(GITHUB_TO_JIRA_TRANSFORMER)
          .end()
          .to(ROUTE_EXITPOINT);
    }

}
