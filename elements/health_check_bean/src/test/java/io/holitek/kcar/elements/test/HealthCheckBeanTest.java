package io.holitek.kcar.elements.test;


import io.holitek.kcar.elements.HealthCheckBean;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class HealthCheckBeanTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckBeanTest.class);

    private static final Map<String, String> statusOkMap = new HashMap<>();
    private static final Map<String, String> statusFaultMap = new HashMap<>();


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

    @BeforeAll
    static void beforeAll() {
        statusOkMap.put(HealthCheckBean.STATUS_KEY, HealthCheckBean.STATUS_OK);
        statusFaultMap.put(HealthCheckBean.STATUS_KEY, HealthCheckBean.STATUS_FAULT);
    }

    @BeforeEach
    void beforeEach() {
        context().getRegistry()
                 .bind(HealthCheckBean.CAMEL_REGISTRY_ID, new HealthCheckBean());

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
                        .bean(HealthCheckBean.CAMEL_REGISTRY_ID)
                        .to("mock:result");
            }
        };
    }


    //
    // tests

    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived(statusOkMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks that setting state to 'fault' works")
    public void testSetFault() throws Exception {
        context().getRegistry()
                 .lookupByNameAndType(HealthCheckBean.CAMEL_REGISTRY_ID, HealthCheckBean.class)
                 .setFaultState();

        getMockEndpoint("mock:result").expectedBodiesReceived(statusFaultMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks that setting state to 'fault' and back to 'ok' works")
    public void testSetOK() throws Exception {

        template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckBean.CAMEL_REGISTRY_ID, HealthCheckBean.class)
                .setFaultState();

        template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckBean.CAMEL_REGISTRY_ID, HealthCheckBean.class)
                .setOkState();

        getMockEndpoint("mock:result").expectedBodiesReceived(statusOkMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

}