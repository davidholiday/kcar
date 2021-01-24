package io.holitek.kcar.routes;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class GithubToJiraRouteTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraRouteTest.class);

    private static final String EXPECTED_NO_CURSOR_GRAPHQL_QUERY =
            "{viewer{organization(login: \"life360\"){repositories(first: 100, ){pageInfo{hasNextPage}nodes " +
                    "{ name pullRequests(first: 100, labels: \"dependencies\", states: OPEN) { nodes { title url } } " +
                    "vulnerabilityAlerts(first: 100) { nodes { securityVulnerability { advisory { description summary "+
                    "severity references { url } ghsaId origin permalink } } } } } } } } }";

    private static final String EXPECTED_CURSOR_GRAPHQL_QUERY =
            "{viewer{organization(login: \"life360\"){repositories(first: 100, after:foo){pageInfo{hasNextPage}nodes " +
                    "{ name pullRequests(first: 100, labels: \"dependencies\", states: OPEN) { nodes { title url } } " +
                    "vulnerabilityAlerts(first: 100) { nodes { securityVulnerability { advisory { description summary "+
                    "severity references { url } ghsaId origin permalink } } } } } } } } }";


    //
    // test setup and configuration

    /**
     * tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
     * doesn't start before we've set up the camel registry and routes.
     *
     * @return
     */
    @Override
    public boolean isUseAdviceWith() { return true; }

    @BeforeEach
    void beforeEach() {
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, GithubToJiraRoute.NAMESPACE_KEY);
        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() { return new GithubToJiraRoute(); }


    //
    // tests

    @Test
    @DisplayName("checks happy path")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived(EXPECTED_NO_CURSOR_GRAPHQL_QUERY);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks after cursor gets inserted property into the velocity template")
    public void testWithAfterCursor() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived(EXPECTED_CURSOR_GRAPHQL_QUERY);
        template.sendBodyAndHeader("direct:start", "", "afterCursor", "after:foo");
        assertMockEndpointsSatisfied();
    }

}
