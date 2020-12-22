package io.holitek.kcar.elements.test;


import io.holitek.kcar.elements.HealthCheckProcessor;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HealthCheckProcessorTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckProcessorTest.class);

    private HealthCheckProcessor healthCheckProcessor;

    @BeforeEach
    void beforeEach() {
        healthCheckProcessor = new HealthCheckProcessor();
        //template.getCamelContext().getRegistry().
        template.getCamelContext().getRegistry().bind(HealthCheckProcessor.CAMEL_REGISTRY_ID, healthCheckProcessor);
    }

    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived(HealthCheckProcessor.OK_JSON_RESPONSE);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Test
    @DisplayName("checks that setting fault flag actually sets fault state")
    public void testFaultPath() throws Exception {

        template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckProcessor.CAMEL_REGISTRY_ID, HealthCheckProcessor.class)
                .setFaultState();

        HealthCheckProcessor bean = template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckProcessor.CAMEL_REGISTRY_ID, HealthCheckProcessor.class);

        LOG.info(bean.toString());
        bean.setFaultState();

        bean = template.getCamelContext()
                .getRegistry()
                .lookupByNameAndType(HealthCheckProcessor.CAMEL_REGISTRY_ID, HealthCheckProcessor.class);

        LOG.info(bean.toString());


        getMockEndpoint("mock:result").expectedBodiesReceived(HealthCheckProcessor.FAULT_JSON_RESPONSE);
        template.sendBody("direct:start", "");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:start")
                        .to("bean:" + HealthCheckProcessor.CAMEL_REGISTRY_ID)
                        .to("mock:result");
            }
        };
    }

}