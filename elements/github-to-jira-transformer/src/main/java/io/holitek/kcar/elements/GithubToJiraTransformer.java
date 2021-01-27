package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.Optional;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;


/**
 * grabs the paginated github responses stored in a state bean and translates them into jira ticket payloads
 */
public class GithubToJiraTransformer implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraTransformer.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(GithubToJiraTransformer.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception {
        Optional<String> paginatedGithubResponseOptional = (Optional<String>)exchange.getMessage().getBody();
        if (paginatedGithubResponseOptional.isEmpty()) { throw new InvalidPayloadException(exchange, Optional.class); }
        String paginatedGithubResponse = paginatedGithubResponseOptional.get();

        LOG.info("paginatedGithubResponse is: {}", paginatedGithubResponse);
        ReadContext ctx = JsonPath.parse(paginatedGithubResponse);

        int nodeCount = ctx.read("$.vulnerabilityAlerts.nodes.length()");
        for (int k = 0; k < nodeCount; k ++) {
            LOG.info(ctx.read("$.vulnerabilityAlerts.nodes[" + k + "]"));
        }

    }

}
