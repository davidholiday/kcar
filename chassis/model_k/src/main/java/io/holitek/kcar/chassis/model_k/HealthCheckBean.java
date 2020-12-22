package io.holitek.kcar.chassis.model_k;


import org.apache.camel.Handler;

/**
 * bean capable of responding with either json-encoded status 'ok' or 'fault'. a fault state is meant to indicate
 * that the service is up but something bad happened that someone should look into...
 *
 */
public class HealthCheckBean {

    public static final String OK_JSON_RESPONSE = "{\"status\": \"ok\"}";
    public static final String FAULT_JSON_REPONSE = "{\"status\": \"fault\"}";

    private boolean isOK = true;

    public HealthCheckBean(boolean defaultState) {
        isOK = defaultState;
    }

    public void setOkState() { isOK = true; }
    public void setFaultState() { isOK = false; }

    @Handler
    public String getHealthCheckJsonString(String body) {
        return isOK ? OK_JSON_RESPONSE : FAULT_JSON_REPONSE;
    }

}
