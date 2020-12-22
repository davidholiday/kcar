package io.holitek.kcar.components.test;


import io.holitek.kcar.components.HealthCheckBean;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class HealthCheckBeanTest extends CamelTestSupport {

    public static

    private HealthCheckBean healthCheckBean;

    @BeforeEach
    void beforeEach() {
        healthCheckBean = new HealthCheckBean();
        template.getCamelContext().getRegistry().bind();
    }

    @Test
    @DisplayName("checks default behavior of HealthCheckBean")
    public void testHappyPath() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");

        template.sendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:start")
                        to("bean:")
                        .to("mock:result");
            }
        };
    }

}