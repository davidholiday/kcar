package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.net.HttpURLConnection;


/**
 * stateful processor that assembles a RESTful JSON response with status as either 'ok' or 'fault'
 */
public class HealthCheckProcessor implements Processor {

    public static final String CAMEL_REGISTRY_ID = HealthCheckProcessor.class.getCanonicalName();

    public static final String HTTP_OK = Integer.valueOf(java.net.HttpURLConnection.HTTP_OK).toString();
    public static final String HTTP_ACCEPTED = Integer.valueOf(HttpURLConnection.HTTP_ACCEPTED).toString();
    public static final String JSON_MEDIA_TYPE = "application/json";

    public static final String OK_JSON_RESPONSE = "{\"status\": \"ok\"}";
    public static final String FAULT_JSON_RESPONSE = "{\"status\": \"fault\"}";

    private boolean isOK = true;

    /**
     *
     * @return
     */
    public boolean getState() { return isOK; }

    /**
     * sets health check state to OK
     */
    public void setOkState() { isOK = true; }

    /**
     * sets health check state to FAULT
     */
    public void setFaultState() { isOK = false; }

    /**
     * handler for camel messages.
     *
     * @param exchange
     * @throws Exception
     */
    public void process(Exchange exchange) throws Exception {
        if (isOK) {
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, JSON_MEDIA_TYPE);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HTTP_OK);
            exchange.getMessage().setBody(OK_JSON_RESPONSE);
        } else {
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, JSON_MEDIA_TYPE);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HTTP_ACCEPTED);
            exchange.getMessage().setBody(FAULT_JSON_RESPONSE);
        }
    }
}
