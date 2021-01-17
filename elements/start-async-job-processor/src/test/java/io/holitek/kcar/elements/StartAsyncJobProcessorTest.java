package io.holitek.kcar.elements;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class StartAsyncJobProcessorTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(StartAsyncJobProcessorTest.class);

    private String asyncBody = "";

    //
    // test setup and configuration


    // tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
    // doesn't start before we've set up the camel registry and routes.
    @Override
    public boolean isUseAdviceWith() { return true; }

    @BeforeEach
    void beforeEach() {
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, StartAsyncJobProcessor.NAMESPACE_KEY);

        asyncBody = CamelPropertyHelper.resolvePropertyOrElseEmpty(
                context,
                "startAsyncJobProcessor.backgroundJobBody"
        );

        context().start();
    }

    @AfterEach
    void afterEach() { context().stop(); }


    @Override
    protected RoutesBuilder[] createRouteBuilders() {
        // route that wraps processor
        RouteBuilder testRoute = new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .process(new StartAsyncJobProcessor())
                        .to("mock:result");
            }
        };

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
    @DisplayName("checks that what goes in matches what comes out")
    public void testHappyPath() throws Exception {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:result");
        MockEndpoint asyncMockEndpoint = getMockEndpoint("mock:asyncResult");

        // what comes back from the main route is a String representing a UUID
        // ty SO https://stackoverflow.com/a/37616347
        mockEndpoint.allMessages().body().isInstanceOf(String.class);
        mockEndpoint.allMessages().body().regex("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

        // the test setup attaches an empty passthru route as the async target of the processor. as such, what we put
        // in should also come back out
        asyncMockEndpoint.expectedBodiesReceived(asyncBody);

        sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }


}
