package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * ready-bake processor for your business logic needs
 */
public class TestProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TestProcessor.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(TestProcessor.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception {

        /*

        PUT BUSINESS LOGIC HERE

         */

        }

}
