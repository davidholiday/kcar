package io.holitek.kcar.elements;


import org.apache.camel.builder.RouteBuilder;
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
public class NoOpProcessorTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpProcessorTest.class);

    //
    // test setup and configuration

    /**
     * tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
     * doesn't start before we've set up the camel registry and routes.
     *
     * @return
     */
    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    /**
     * fires up the camel context
     */
    @BeforeEach
    void beforeEach() { context().start(); }

    /**
     * stops the camel context
     */
    @AfterEach
    void afterEach() { context().stop(); }

    /**
     * wraps the element in a camel route that has test hooks for ingress and egress
     *
     * @return
     */
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .process(new NoOpProcessor())
                        .to("mock:result");
            }
        };
    }


    //
    // tests

    @Test
    @DisplayName("checks that what goes in matches what comes out")
    public void testHappyPath() throws Exception {
        String input = "mrs input";
        getMockEndpoint("mock:result").expectedBodiesReceived(input);
        sendBody("direct:start", input);
        assertMockEndpointsSatisfied();
    }


}
