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
public class HelloWorldRouteTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldRouteTest.class);


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
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, HelloWorldRoute.NAMESPACE_KEY);
        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() { return new HelloWorldRoute(); }


    //
    // tests

    @Test
    @DisplayName("checks happy path")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                "200"
        );

        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World!");
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

}
