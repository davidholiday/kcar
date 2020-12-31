package io.holitek.kcar.helpers;


import org.apache.camel.builder.RouteBuilder;


public class MockRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start").to("mock:out");
    }

}
