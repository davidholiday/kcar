package io.holitek.kcar.components;

import org.apache.camel.Handler;


/**
 * bean capable of responding with either json-encoded status 'ok' or 'fault'. a fault state is meant to indicate
 * that the service is up but something bad happened that someone should look into...
 *
 */
public class HealthCheckBean {

    public static final String CAMEL_REGISTRY_ID = HealthCheckBean.class.getCanonicalName();

    public static final String OK_JSON_RESPONSE = "{\"status\": \"ok\"}";
    public static final String FAULT_JSON_REPONSE = "{\"status\": \"fault\"}";

    private boolean isOK = true;

    /**
     * sets health check state to OK
     */
    public void setOkState() { isOK = true; }

    /**
     * sets health check state to FAULT
     */
    public void setFaultState() { isOK = false; }

    /**
     * returns JSON response strong appropriate to state of member variable isOK. is also the default exchange message
     * handler.
     *
     * @param body
     * @return
     */
    @Handler
    public String getHealthCheckJsonString(String body) {
        return isOK ? OK_JSON_RESPONSE : FAULT_JSON_REPONSE;
    }

}