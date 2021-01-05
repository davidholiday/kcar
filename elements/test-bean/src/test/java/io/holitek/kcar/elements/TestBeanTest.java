package io.holitek.kcar.elements;


import io.holitek.kcar.elements.TestBean;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class TestBeanTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(TestBeanTest.class);


    //
    // test setup and configuration

    // tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
    // doesn't start before we've set up the camel registry and routes.
    @Override
    public boolean isUseAdviceWith() { return true; }


    @BeforeEach
    void beforeEach() {
        context().getRegistry()
                 .bind(TestBean.NAMESPACE_KEY, new TestBean());

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
                        .bean(TestBean.NAMESPACE_KEY)
                        .to("mock:result");
            }
        };
    }


    //
    // tests

    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("bar");
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

}