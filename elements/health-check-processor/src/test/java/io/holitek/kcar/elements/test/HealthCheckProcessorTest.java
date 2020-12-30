package io.holitek.kcar.elements.test;


import io.holitek.kcar.elements.HealthCheckProcessor;

import org.apache.camel.CamelExecutionException;
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
public class HealthCheckProcessorTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckProcessorTest.class);

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

    /**
     * populate status maps. purposefully NOT using the constants found in the HealthCheckBean. the Processor this
     * set of tests covers only looks for a specific key. both the processor and this test should not (and therefore
     * do not as of this writing) have dependencies on any other elements.
     */
    @BeforeAll
    static void beforeAll() {
        statusOkMap.put(HealthCheckProcessor.STATUS_KEY, "ok");
        statusFaultMap.put(HealthCheckProcessor.STATUS_KEY, "fault");
    }

    @BeforeEach
    void beforeEach() { context().start(); }

    @AfterEach
    void afterEach() { context().stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .process(new HealthCheckProcessor())
                        .to("mock:result");
            }
        };
    }


    //
    // tests

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

}