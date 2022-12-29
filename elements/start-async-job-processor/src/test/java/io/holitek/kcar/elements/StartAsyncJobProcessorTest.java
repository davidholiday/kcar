package io.holitek.kcar.elements;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;


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
        mockEndpoint.allMessages().body().isInstanceOf(Map.class);

        // the test setup attaches an empty passthru route as the async target of the processor. as such, what we put
        // in should also come back out
        asyncMockEndpoint.expectedBodiesReceived(asyncBody);

        sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    @DisplayName("inspects that the processor creates a map with a jobid and uuid ")
    public void testResultMap() throws Exception {
        // this is a bit of a hack to ensure the async route (the one the processor is going to look for) is created
        sendBody("direct:start", "");

        // exercise the processor
        StartAsyncJobProcessor startAsyncJobProcessor = new StartAsyncJobProcessor();
        Exchange mrExchange = createExchangeWithBody("");
        startAsyncJobProcessor.process(mrExchange);

        // ensure what comes back is what we expect
        Map<String, String> resultMap = mrExchange.getMessage().getBody(Map.class);
        Assertions.assertTrue(resultMap.keySet().contains("jobID"));

        String shouldBeUUID = resultMap.get("jobID");
        // ty SO https://stackoverflow.com/a/37616347
        Assertions.assertTrue(shouldBeUUID.matches("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})"));
    }

}
