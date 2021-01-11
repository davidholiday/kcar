package io.holitek.kcar.routes;


import io.holitek.kcar.elements.HealthCheckBean;
import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.helpers.CamelPropertyHelper;
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
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, HealthCheckRoute.NAMESPACE_KEY);

        HealthCheckBean healthCheckBean = new HealthCheckBean();
        context.getRegistry().bind(HealthCheckBean.NAMESPACE_KEY, healthCheckBean);

        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }

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

        context.getRegistry()
               .lookupByNameAndType(HealthCheckBean.NAMESPACE_KEY, HealthCheckBean.class)
               .setFaultState();

        getMockEndpoint("mock:result").expectedBodiesReceived(statusFaultMap);
        sendBody("direct:start", "");
        assertMockEndpointsSatisfied();

    }


}
