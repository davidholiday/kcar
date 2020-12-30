package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * a passthru element that can be replaced with a mock endpoint (for testing) via injection
 */
public class NoOpProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpProcessor.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(NoOpProcessor.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception { /* noop */ }

}
