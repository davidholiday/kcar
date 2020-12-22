package io.holitek.kcar.chassis.model_k.components;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import static io.holitek.kcar.chassis.model_k.common.HttpStrings.JSON_MEDIA_TYPE;
import static io.holitek.kcar.chassis.model_k.common.HttpStrings.HTTP_OK;
import static io.holitek.kcar.chassis.model_k.common.HttpStrings.HTTP_ACCEPTED;


/**
 * stateful processor that assembles a RESTful JSON response with status as either 'ok' or 'fault'
 */
public class HealthCheckProcessor implements Processor {

    public static final String OK_JSON_RESPONSE = "{\"status\": \"ok\"}";
    public static final String FAULT_JSON_REPONSE = "{\"status\": \"fault\"}";

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
            exchange.getMessage().setBody(FAULT_JSON_REPONSE);
        }
    }
}
