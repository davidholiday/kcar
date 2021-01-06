#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;


/**
 * ready-bake processor for your business logic needs
 */
public class ${artifactIdCamelCase} implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(${artifactIdCamelCase}.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(${artifactIdCamelCase}.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception {

        /*

        PUT BUSINESS LOGIC HERE

         */

        }

}
