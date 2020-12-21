package io.holitek.kcar.chassis.model_k;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.support.DefaultRegistry;

import org.apache.camel.component.servletlistener.CamelContextLifecycle;
import org.apache.camel.component.servletlistener.ServletCamelContext;


public class AppContextLifecycle implements CamelContextLifecycle<DefaultRegistry> {

    @Override
    public void beforeStart(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

    @Override
    public void afterStart(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

    @Override
    public void beforeAddRoutes(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

    @Override
    public void afterAddRoutes(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

    @Override
    public void beforeStop(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

    @Override
    public void afterStop(ServletCamelContext camelContext, DefaultRegistry registry) throws Exception {}

}