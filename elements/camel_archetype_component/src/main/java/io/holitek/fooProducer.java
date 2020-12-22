package io.holitek;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fooProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(fooProducer.class);
    private fooEndpoint endpoint;

    public fooProducer(fooEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
