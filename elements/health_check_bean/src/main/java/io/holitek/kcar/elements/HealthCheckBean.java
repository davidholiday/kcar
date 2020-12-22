package io.holitek.kcar.elements;


import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * a bean-wrapped map containing app health information. currently acts as a boolean indicating things are ok or
 * something weird happened (but the system is still functional enough to service the request). this can be extended
 * in future to include more information, as well as conditional logic to present subsets of the available data
 * depending on caller entitlements.
 */
public class HealthCheckBean {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckBean.class);

    public static final String CAMEL_REGISTRY_ID = HealthCheckBean.class.getCanonicalName();

    public static final String STATUS_KEY = "status";
    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAULT = "fault";

    private final Map<String, String> stateMap = new HashMap<>();

    public HealthCheckBean() { stateMap.put(STATUS_KEY, STATUS_OK); }

    /**
     * sets health check state to OK
     */
    public void setOkState() { stateMap.put(STATUS_KEY, STATUS_OK); }

    /**
     * sets health check state to FAULT
     */
    public void setFaultState() { stateMap.put(STATUS_KEY, STATUS_FAULT); }

    /**
     * clones and returns the stateMap object
     */
    public Map<String, String> getState(Exchange exchange) {
        Map<String, String> rv = new HashMap<>();
        for (String key : stateMap.keySet()) {
            rv.put(key, stateMap.get(key));
        }
        return rv;
    }

}