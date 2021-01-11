package io.holitek.kcar.elements;


import io.holitek.kcar.elements.HelloWorldBean;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * example testing setup for a camel element
 */
public class HelloWorldBeanTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldBeanTest.class);


    //
    // test setup and configuration

    // tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
    // doesn't start before we've set up the camel registry and routes.
    @Override
    public boolean isUseAdviceWith() { return true; }


    @BeforeEach
    void beforeEach() {
        context().getRegistry()
                 .bind(HelloWorldBean.NAMESPACE_KEY, new HelloWorldBean());

        context().start();
    }

    @AfterEach
    void afterEach() { context().stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .bean(HelloWorldBean.NAMESPACE_KEY)
                        .to("mock:result");
            }
        };
    }


    //
    // tests

    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World!");
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

}