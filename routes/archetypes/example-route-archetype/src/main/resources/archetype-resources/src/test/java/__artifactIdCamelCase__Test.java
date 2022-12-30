#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import io.holitek.kcar.elements.HealthCheckBean;
import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.helpers.CamelPropertyHelper;
import org.apache.camel.component.mock.MockEndpoint;
import ${groupId}.${artifactIdCamelCase};

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
public class ${artifactIdCamelCase}Test extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(${artifactIdCamelCase}Test.class);

private static final String statusOkJson = "{\"" + HealthCheckProcessor.STATUS_KEY + "\":\"ok\"}";
private static final String statusFaultJson = "{\"" + HealthCheckProcessor.STATUS_KEY + "\":\"fault\"}";


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

    @BeforeEach
    void beforeEach() {
        CamelPropertyHelper.loadTestPropertyFileForNamespace(context, ${artifactIdCamelCase}.NAMESPACE_KEY);

        HealthCheckBean healthCheckBean = new HealthCheckBean();
        context.getRegistry().bind(HealthCheckBean.NAMESPACE_KEY, healthCheckBean);

        context.start();
    }

    @AfterEach
    void afterEach() { context.stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() { return new ${artifactIdCamelCase}(); }


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

        getMockEndpoint("mock:result").expectedBodiesReceived(statusOkJson);
        template.sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
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

        getMockEndpoint("mock:result").expectedBodiesReceived(statusFaultJson);
        sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);

    }


}
