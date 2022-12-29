package io.holitek.kcar.elements;


import io.holitek.kcar.elements.PaginatedResponseBean;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.component.mock.MockEndpoint;

import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 *
 */
public class PaginatedResponseBeanTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(PaginatedResponseBeanTest.class);


    //
    // test setup and configuration

    // tells the test runner that we'll start and stop the camel context manually. this ensures the camel context
    // doesn't start before we've set up the camel registry and routes.
    @Override
    public boolean isUseAdviceWith() { return true; }


    @BeforeEach
    void beforeEach() {

        // the data store in the bean is static to ensure that there is only one instance of the data store when
        // camel spins up multiple instances of the object
        PaginatedResponseBean paginatedResponseBean = new PaginatedResponseBean();
        paginatedResponseBean.clearPaginatedResponses();

        context().getRegistry()
                 .bind(PaginatedResponseBean.NAMESPACE_KEY, new PaginatedResponseBean());

        context().start();
    }

    @AfterEach
    void afterEach() { context().stop(); }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .bean(PaginatedResponseBean.NAMESPACE_KEY)
                        .to("mock:result");
            }
        };
    }


    //
    // tests
    @Test
    @DisplayName("checks default behavior")
    public void testHappyPath() throws Exception {
        Optional<String> expectedResult = Optional.empty();
        getMockEndpoint("mock:result").allMessages().body(Optional.class);
        getMockEndpoint("mock:result").allMessages().body().equals(expectedResult);
        template.sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    @DisplayName("checks that bean contents can be modified ")
    public void testAddValue() throws Exception {
        ((PaginatedResponseBean)context.getRegistry()
                                       .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                                       .pushPaginatedResponse("{\"foo\":\"bar\"}");

        Optional<String> expectedResult = Optional.of("{\"foo\":\"bar\"}");
        getMockEndpoint("mock:result").allMessages().body(Optional.class);
        getMockEndpoint("mock:result").allMessages().body().equals(expectedResult);
        template.sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    @DisplayName("checks that bean contents are cleared properly")
    public void testClearValue() throws Exception {
        ((PaginatedResponseBean)context.getRegistry()
                .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                .pushPaginatedResponse("{\"foo\":\"bar\"}");

        ((PaginatedResponseBean)context.getRegistry()
                .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                .clearPaginatedResponses();

        Optional<String> expectedResult = Optional.empty();
        getMockEndpoint("mock:result").allMessages().body(Optional.class);
        getMockEndpoint("mock:result").allMessages().body().equals(expectedResult);
        template.sendBody("direct:start", "");
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    @DisplayName("checks that the bean reports the correct number of elements in its internal queue ")
    public void testGetPaginatedResponseSize() {
        ((PaginatedResponseBean)context.getRegistry()
                .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                .pushPaginatedResponse("{\"foo\":\"bar\"}");

        int expectedNumberOfPaginatedResponses = 1;

        int actualNumberOfPaginatedResponses =
                ((PaginatedResponseBean)context.getRegistry()
                                               .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                                               .getNumberOfPaginatedResponses();

        Assertions.assertEquals(expectedNumberOfPaginatedResponses, actualNumberOfPaginatedResponses);

        ((PaginatedResponseBean)context.getRegistry()
                                       .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                                       .popPaginatedResponse();

        expectedNumberOfPaginatedResponses = 0;

        actualNumberOfPaginatedResponses =
                ((PaginatedResponseBean)context.getRegistry()
                                               .lookupByName(PaginatedResponseBean.NAMESPACE_KEY))
                                               .getNumberOfPaginatedResponses();

        Assertions.assertEquals(expectedNumberOfPaginatedResponses, actualNumberOfPaginatedResponses);

    }


}