package io.holitek.kcar.elements;


import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * a state bean containing app health information. currently acts as a boolean indicating things are ok or
 * something weird happened (but the system is still functional enough to service the request). this can be extended
 * in future to include more information.
 */
public class HealthCheckBean {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckBean.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(HealthCheckBean.class.getSimpleName());

    public static final String STATUS_KEY = "status";
    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAULT = "fault";

    // this, in all the ways that matter, turn the bean into a stateful singleton. all routes can dymanically create
    // their own instance of this bean while - in a thread safe manner - accessing the same underlying app health
    // data structure.
    private static final Map<String, String> stateMap = new ConcurrentHashMap<>();

    /**
     * constructs instance with default stateMap entry {'status':'ok'}
     */
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
     * clones and returns the stateMap object. takes Exchange as a parameter to let camel know this method is the
     * message handler.
     */
    public Map<String, String> getState(Exchange exchange) {
        Map<String, String> rv = new HashMap<>();
        for (String key : stateMap.keySet()) {
            rv.put(key, stateMap.get(key));
        }
        return rv;
    }

}