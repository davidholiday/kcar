package io.holitek.kcar.routes.test;


import io.holitek.kcar.elements.HealthCheckBean;
import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.routes.HealthCheckRoute;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.jndi.JndiContext;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
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
     * tested. done post camel context setup - which means it should happen before each and every test.
     *
     * @TODO this method can be converted into a common test utils helper. something like
     *   <p>
     *     public void setUpCamel(CamelContext camelContext, List<String> properties)
     *   </p>
     *   where properties is populated with whatever the route says it needs. the method can also stand up the
     *   test route.
     *   put this in a class that extends Camel-Test-Support and have all your tests extend from there.
     *   see ----v when it's time ...
     *   https://www.javadoc.io/doc/org.apache.camel/camel-test/latest/org/apache/camel/test/junit4/CamelTestSupport.html
     */
    @Override
    protected void doPostSetup() {
        // load test properties file. not only gives us flexibility in configuring the tests but also (and perhaps more
        // importantly) the routes are written in a way to where they expect properties to be set in the camel context.
        context().getPropertiesComponent()
                 .setLocation("classpath:application.test.properties");

        // resolve element classnames so needed elements can be created and added to the camel registry
        //
        String healthCheckBeanClassName =
                context().getPropertiesComponent()
                         .resolveProperty(HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID)
                         .orElse("");

        // create object instances for whatever is specified in properties file
        //
        try {
            LOG.info("adding {} -> {} to the registry...",
                    healthCheckBeanClassName, healthCheckBeanClassName);

            context().getRegistry()
                     .bind(
                             //HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID,
                             healthCheckBeanClassName,
                             Class.forName(healthCheckBeanClassName).newInstance()
                     );
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.error("couldn't create class instance from entry in properties file", e);
        }

        // add the route we want to test
        //
        LOG.info("firing up routes");
        try {
            context().addRoutes(getTestRoute());
            //context().addRoutes(getTestRouteWrapper());
        } catch (Exception e) {
            LOG.error("couldn't add route for testing", e);
        }

    }





    /**
     *
     * @throws Exception
     */
    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        context.start();

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

        context.stop();
    }


    @Override
    public boolean isUseAdviceWith() {
        return true;
    }


    /**
     *
     * @throws Exception
     */
    @Test
    @DisplayName("checks fault behavior")
    public void testFaultPath() throws Exception {
        context.start();

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.HTTP_RESPONSE_CODE,
                HealthCheckProcessor.HTTP_OK
        );

        getMockEndpoint("mock:result").expectedHeaderReceived(
                Exchange.CONTENT_TYPE,
                HealthCheckProcessor.JSON_MEDIA_TYPE
        );

        getMockEndpoint("mock:result").expectedBodiesReceived(statusFaultMap);

        String healthCheckBeanClassName =
                context()
                        .getPropertiesComponent()
                        .resolveProperty(HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID)
                        .orElse("");

        LOG.info("healthCheckBeanClassName is: {}", healthCheckBeanClassName);

        LOG.info(template.getCamelContext().getRegistry().lookupByName(healthCheckBeanClassName) + "");
        LOG.info(context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class) + "");
        context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).setFaultState();
        LOG.info(context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).getState(null) + "");
        LOG.info(template.getCamelContext().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).toString());

        sendBody("direct:start", "");
//        LOG.info(template.getCamelContext().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).toString());
        assertMockEndpointsSatisfied();

        context().stop();
    }


    /**
     *
     * @return
     */
    private RoutesBuilder getTestRoute() { return new HealthCheckRoute(); }

    /**
     * allows us to create a fresh instance of the route (including whatever we're injecting into it) for every test.
     *
     * @return
     * @throws Exception
     */
    private RoutesBuilder getTestRouteWrapper() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .to("direct:" + HealthCheckRoute.ROUTE_ID)
                        .to("mock:result");
            }
        };

    }


}



        /*



        https://opensourceconnections.com/blog/2014/04/24/correctly-using-camels-advicewith-in-unit-tests/


        that was a fucking livesaver!!!!


        TODO the tl'dr on this was that your assumption about how beans work is CORRECT!! something weird was happening in the guts of the CameTestSupport logic that was causing the root to get
          resolved before the registry was properly populated. use the method in that article to start/stop the camel context before and after each test - MANUALLY.

          TODO also - write a version on this test that invovles using "advicewith" so you have working examples using both methods



         */