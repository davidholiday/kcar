package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * a passthru element that can be replaced with a mock endpoint (for testing) via injection
 */
public class NoOpProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpProcessor.class);

    public static final String CAMEL_REGISTRY_ID = NoOpProcessor.class.getCanonicalName();

    @Override
    public void process(Exchange exchange) throws Exception { /* noop */ }

}
