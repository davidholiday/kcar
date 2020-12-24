package io.holitek.kcar.routes.test;


import io.holitek.kcar.elements.HealthCheckBean;
import io.holitek.kcar.elements.HealthCheckProcessor;
import io.holitek.kcar.routes.HealthCheckRoute;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
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


        /*



        TODO this is weird and hopefully because of the way the test support stuff works rather than camel itself. basically you can't seem to use the below code to pre-stage or otherwise manipulate the registry. whatever bean ovject gets created at route-creation time seems to be indepenant from whatever is going on with the registry you have a hook into.

        TODO at this point you need to do some experimentation on a live camel route to see the degree to which objects will be sourced from the registry first before the route makes it's own. also where is the route hanging onto these things if not in the registry? shouldn't there be a collision?

        TODO either way - you ened to be able to test routes end2end w/o mocking any of the intermediary elements.




         */




//        // resolve element classnames so needed elements can be created and added to the camel registry
//        //
//        String healthCheckBeanClassName =
//                context().getPropertiesComponent()
//                         .resolveProperty(HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID)
//                         .orElse("");
//
//        String healthCheckProcessorClassName =
//                context().getPropertiesComponent()
//                         .resolveProperty(HealthCheckRoute.HEALTH_CHECK_PROCESSOR_PROPERTY_ID)
//                         .orElse("");
//
//
//        // create object instances for whatever is specified in properties file
//        //
//        try {
//            LOG.info("adding {} -> {} to the registry...",
//                    healthCheckBeanClassName, healthCheckBeanClassName);
//
//            context().getRegistry()
//                     .bind(
//                             //HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID,
//                             healthCheckBeanClassName,
//                             Class.forName(healthCheckBeanClassName).newInstance()
//                     );
//
//
//            LOG.info("adding {} -> {} to the registry",
//                    healthCheckProcessorClassName, healthCheckProcessorClassName);
//
//            context().getRegistry()
//                     .bind(
//                             //HealthCheckRoute.HEALTH_CHECK_PROCESSOR_PROPERTY_ID,
//                             healthCheckProcessorClassName,
//                             Class.forName(healthCheckProcessorClassName)
//                    );
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            LOG.error("couldn't create class instance from entry in properties file", e);
//        }


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


    /**
     *
     * @throws Exception
     */
    @Test
    @DisplayName("checks fault behavior")
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

//        String healthCheckBeanClassName =
//                context()
//                        .getPropertiesComponent()
//                        .resolveProperty(HealthCheckRoute.HEALTH_CHECK_BEAN_PROPERTY_ID)
//                        .orElse("");
//
//        LOG.info("healthCheckBeanClassName is: {}", healthCheckBeanClassName);
//
//        LOG.info(template.getCamelContext().getRegistry().lookupByName(healthCheckBeanClassName) + "");
//        LOG.info(context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class) + "");
//        context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).setFaultState();
//        LOG.info(context().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).getState(null) + "");
//        LOG.info(template.getCamelContext().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).toString());

        sendBody("direct:start", "");
//        LOG.info(template.getCamelContext().getRegistry().lookupByNameAndType(healthCheckBeanClassName, HealthCheckBean.class).toString());
        assertMockEndpointsSatisfied();
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
//    private RoutesBuilder getTestRouteWrapper() {
//        return new RouteBuilder() {
//            @Override
//            public void configure() {
//                from("direct:start")
//                        .to("direct:" + HealthCheckRoute.ROUTE_ID)
//                        .to("mock:result");
//            }
//        };
//
//    }


}
