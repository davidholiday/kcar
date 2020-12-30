package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;

import java.beans.Introspector;
import java.util.Map;


/**
 * processor for contents of HealthCheckBean. role is to apply business rules to determine what the caller is entitled
 * to see then send that information in the form of a RESTful JSON response.
 */
public class HealthCheckProcessor implements Processor {

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(HealthCheckProcessor.class.getSimpleName());

    public static final String STATUS_KEY = "status";

    public static final String HTTP_OK = Integer.valueOf(java.net.HttpURLConnection.HTTP_OK).toString();
    public static final String JSON_MEDIA_TYPE = "application/json";


    /**
     *
     * @param exchange
     * @throws Exception
     */
    public void process(Exchange exchange) throws Exception {
        Map<String, String> body = exchange.getMessage().getMandatoryBody(Map.class);

        if (body.containsKey(STATUS_KEY) == false) {
            throw new InvalidPayloadException(exchange, body.getClass());
        }

        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, JSON_MEDIA_TYPE);
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HTTP_OK);
        exchange.getMessage().setBody(body);
    }
}
