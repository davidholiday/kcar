package io.holitek.kcar.routes;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.component.mock.MockEndpoint;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class StartAsyncJobRouteTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(StartAsyncJobRouteTest.class);


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
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, StartAsyncJobRoute.NAMESPACE_KEY);
        //CamelPropertyHelper.loadPropertyFileForNamespace(context, StartAsyncJobProcessor.NAMESPACE_KEY);
        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }


    @Override
    protected RoutesBuilder[] createRouteBuilders() {
        // route that wraps processor
        RouteBuilder testRoute = new StartAsyncJobRoute();

        // target route for async processing
        RouteBuilder asyncTargetRoute = new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:asyncStart")
                        .to("mock:asyncResult");
            }
        };

        RoutesBuilder[] routesBuilderArrays = {testRoute, asyncTargetRoute};
        return routesBuilderArrays;
    }


    //
    // tests

    @Test
    @DisplayName("checks happy path")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                "202"
        );

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.CONTENT_TYPE,
                "application/json"
        );

        // ensure what comes back is json, has the right key, and has a UUID for a value
        // ty SO https://stackoverflow.com/a/37616347
        getMockEndpoint("mock:result").allMessages()
                                          .jsonpath("$.jobID")
                                          .regex("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

        template.sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
    }

}
