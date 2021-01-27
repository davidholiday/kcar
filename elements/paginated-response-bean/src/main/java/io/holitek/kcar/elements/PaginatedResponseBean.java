package io.holitek.kcar.elements;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * container intended for paginated xml or json responses
 */
public class PaginatedResponseBean {

    private static final Logger LOG = LoggerFactory.getLogger(PaginatedResponseBean.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY =
        Introspector.decapitalize(PaginatedResponseBean.class.getSimpleName());

    private final Queue<String> paginatedResponseQueue = new ConcurrentLinkedQueue<>();

    public void pushPaginatedResponse(String paginatedResponse) { paginatedResponseQueue.add(paginatedResponse); }

    public void clearPaginatedResponses() { paginatedResponseQueue.clear(); }

    public int getNumberOfPaginatedResponses() { return paginatedResponseQueue.size(); }

    public Optional<String> popPaginatedResponse() { return Optional.ofNullable(paginatedResponseQueue.poll()); }

}