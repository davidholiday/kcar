package io.holitek.kcar.routes.test;


import io.holitek.kcar.elements.HealthCheckBean;
import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.routes.HealthCheckRoute;

import org.apache.camel.Exchange;
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
public class HealthCheckRouteTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckRouteTest.class);

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
        statusOkMap.put(HealthCheckProcessor.STATUS_KEY, "ok");
        statusFaultMap.put(HealthCheckProcessor.STATUS_KEY, "fault");
    }

    @BeforeEach
    void beforeEach() {
        context().getPropertiesComponent()
                .setLocation("classpath:healthCheckRoute.application.test.properties");

        // we need to add the health check bean to the registry prior to the test so we can change its state prior to
        // the test run. otherwise the bean will be created when the route calls for it and we won't be able to test
        // the processor's response to different health-check states reported by the bean.
        context().getRegistry()
                 .bind(HealthCheckBean.CAMEL_REGISTRY_ID, new HealthCheckBean());

        context().start();
    }

    @AfterEach
    void afterEach() { context().stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() { return new HealthCheckRoute(); }


    //
    // tests

    @Test
    @DisplayName("checks happy path")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                HealthCheckProcessor.HTTP_OK
        );

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.CONTENT_TYPE,
                HealthCheckProcessor.JSON_MEDIA_TYPE
        );

        getMockEndpoint("mock:result").expectedBodiesReceived(statusOkMap);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }


    @Test
    @DisplayName("checks fault state is correctly processed")
    public void testFaultPath() throws Exception {

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                HealthCheckProcessor.HTTP_OK
        );

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.CONTENT_TYPE,
                HealthCheckProcessor.JSON_MEDIA_TYPE
        );

        getMockEndpoint("mock:result").expectedBodiesReceived(statusFaultMap);

        context().getRegistry()
                 .lookupByNameAndType(HealthCheckBean.CAMEL_REGISTRY_ID, HealthCheckBean.class)
                 .setFaultState();

        sendBody("direct:start", "");
        assertMockEndpointsSatisfied();

    }

}
