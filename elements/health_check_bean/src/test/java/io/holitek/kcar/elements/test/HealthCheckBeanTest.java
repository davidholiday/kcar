package io.holitek.kcar.elements.test;


import io.holitek.kcar.elements.HealthCheckBean;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class HealthCheckBeanTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckBeanTest.class);

    private static final Map<String, String> expectedStatusOkMap = new HashMap<>();
    private static final Map<String, String> expectedStatusFaultMap = new HashMap<>();

    private HealthCheckBean healthCheckBean;

    @BeforeAll
    static void beforeAll() {
        expectedStatusOkMap.put(HealthCheckBean.STATUS_KEY, HealthCheckBean.STATUS_OK);
        expectedStatusFaultMap.put(HealthCheckBean.STATUS_KEY, HealthCheckBean.STATUS_FAULT);
    }

    @BeforeEach
    void beforeEach() {
        // ensure bean obj is always fresh
        healthCheckBean = new HealthCheckBean();
        template.getCamelContext().getRegistry().bind(HealthCheckBean.CAMEL_REGISTRY_ID, healthCheckBean);

        // ensure the route is always fresh as well. otherwise, things fail because the beans is stale
        try {
            template.getCamelContext().addRoutes(getTestRoute());
        } catch(Exception e) {
            LOG.error("something went wrong adding routes", e);
        }
    };

    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived(expectedStatusOkMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks that setting state to 'fault' works")
    public void testSetFault() throws Exception {

        template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckBean.CAMEL_REGISTRY_ID, HealthCheckBean.class)
                .setFaultState();

        getMockEndpoint("mock:result").expectedBodiesReceived(expectedStatusFaultMap);
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

        getMockEndpoint("mock:result").expectedBodiesReceived(expectedStatusOkMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    /**
     * allows us to create a fresh instance of the route (including whatever we're injecting into it) for every test.
     *
     * @return
     * @throws Exception
     */
    private RoutesBuilder getTestRoute() throws Exception {

        RouteBuilder routeBuilder = null;

        try {
            routeBuilder = new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:start")
                            .to("bean:" + HealthCheckBean.CAMEL_REGISTRY_ID)
                            .to("mock:result");
                }
            };
        } catch (Exception e) {
            LOG.error("something went wring building the routes", e);
        }

        return routeBuilder;
    }

}