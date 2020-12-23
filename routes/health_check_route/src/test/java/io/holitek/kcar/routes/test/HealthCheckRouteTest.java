package io.holitek.kcar.routes.test;


import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.routes.HealthCheckRoute;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
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


    /**
     *
     */
    @BeforeAll
    static void beforeAll() {
        statusOkMap.put("status", "ok");
        statusFaultMap.put("status", "fault");
    }


    /**
     * reads from the test properties file and sets up the camel context with registry entries and the route to be
     * tested.
     */
    @BeforeEach
    void setUpCamel() {
        // load test properties file. not only gives us flexibility in configuring the tests but also (and perhaps more
        // importantly) the routes are written in a way to where they expect properties to be set in the camel context.
        template.getCamelContext()
                .getPropertiesComponent()
                .setLocation("classpath:application.test.properties");

        // resolve element classnames so needed elements can be created and added to the camel registry
        //
        String healthCheckBeanClassName =
                template.getCamelContext()
                        .getPropertiesComponent()
                        .resolveProperty(HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID)
                        .orElse("");

        String healthCheckProcessorClassName =
                template.getCamelContext()
                        .getPropertiesComponent()
                        .resolveProperty(HealthCheckRoute.HEALTH_CHECK_PROCESSOR_PROPERTY_ID)
                        .orElse("");


        // create object instances for whatever is specified in properties file
        //
        try {
            LOG.info("adding {} -> {} to the registry...",
                    HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID, healthCheckBeanClassName);

            template.getCamelContext()
                    .getRegistry()
                    .bind(
                            HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID,
                            Class.forName(healthCheckBeanClassName)
                    );


            LOG.info("adding {} -> {} to the registry",
                    HealthCheckRoute.HEALTH_CHECK_PROCESSOR_PROPERTY_ID, healthCheckProcessorClassName);

            template.getCamelContext()
                    .getRegistry()
                    .bind(
                            HealthCheckRoute.HEALTH_CHECK_PROCESSOR_PROPERTY_ID,
                            Class.forName(healthCheckProcessorClassName)
                    );
        } catch (ClassNotFoundException e) {
            LOG.error("couldn't create class instance from entry in properties file", e);
        }


//        // add the route we want to test
//        //
//        try {
//            template.getCamelContext().addRoutes(new HealthCheckRoute());
//        } catch (Exception e) {
//            LOG.error("couldn't add route for testing", e);
//        }

    }


    /**
     *
     * @throws Exception
     */
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

    /**
     * uses CamelTestSupport to inject the route to be tested
     *
     * @return
     * @throws Exception
     */
    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception { return new HealthCheckRoute(); }

}
