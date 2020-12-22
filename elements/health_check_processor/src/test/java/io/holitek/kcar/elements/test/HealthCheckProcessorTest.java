package io.holitek.kcar.elements.test;


import io.holitek.kcar.elements.HealthCheckProcessor;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
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
public class HealthCheckProcessorTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckProcessorTest.class);

    private static final Map<String, String> statusOkMap = new HashMap<>();
    private static final Map<String, String> statusFaultMap = new HashMap<>();

    private HealthCheckProcessor healthCheckProcessor;

    @BeforeAll
    static void beforeAll() {
        statusOkMap.put("status", "ok");
        statusFaultMap.put("status", "fault");
    }

    @BeforeEach
    void beforeEach() {
        // ensure bean obj is always fresh
        healthCheckProcessor = new HealthCheckProcessor();
        template.getCamelContext().getRegistry().bind(HealthCheckProcessor.CAMEL_REGISTRY_ID, healthCheckProcessor);

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
        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                HealthCheckProcessor.HTTP_OK
        );

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.CONTENT_TYPE,
                HealthCheckProcessor.JSON_MEDIA_TYPE
        );

        getMockEndpoint("mock:result").expectedBodiesReceived(statusOkMap);
        template.sendBody("direct:start", statusOkMap);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks case where health check status is 'fault'")
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
        template.sendBody("direct:start", statusFaultMap);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks exception is thrown when status key is missing from status map in exchange body")
    public void testMissingStatusKey() throws Exception {

        boolean testPass = false;
        try {
            template.sendBody("direct:start", new HashMap<String, String>());
        } catch (CamelExecutionException e) {
            testPass = true;
        }

        Assertions.assertEquals(
                true,
                testPass,
                "processor should throw exception if the status key it expects isn't in in the status map"
        );

    }

    @Test
    @DisplayName("checks exception is thrown when the contents of the message body are of the wrong type")
    public void testWrongBodyType() throws Exception {

        boolean testPass = false;
        try {
            template.sendBody("direct:start", false);
        } catch (CamelExecutionException e) {
            testPass = true;
        }

        Assertions.assertEquals(
                true,
                testPass,
                "processor should throw exception if the status key it expects isn't in in the status map"
        );

    }

    @Test
    @DisplayName("checks exception is thrown when the message body is null")
    public void testNullbody() throws Exception {

        boolean testPass = false;
        try {
            template.sendBody("direct:start", null);
        } catch (CamelExecutionException e) {
            testPass = true;
        }

        Assertions.assertEquals(
                true,
                testPass,
                "processor should throw exception if the message body is empty"
        );

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
                            .to("bean:" + HealthCheckProcessor.CAMEL_REGISTRY_ID)
                            .to("mock:result");
                }
            };
        } catch (Exception e) {
            LOG.error("something went wring building the routes", e);
        }

        return routeBuilder;
    }

}