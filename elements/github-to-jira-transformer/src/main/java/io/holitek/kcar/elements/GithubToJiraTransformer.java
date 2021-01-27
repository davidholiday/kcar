package io.holitek.kcar.elements;


import com.jayway.jsonpath.ReadContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jayway.jsonpath.JsonPath;


/**
 * grabs the paginated github responses stored in a state bean and translates them into jira ticket payloads
 */
public class GithubToJiraTransformer implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraTransformer.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(GithubToJiraTransformer.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception {

            ReadContext ctx = JsonPath.parse(exchange.getMessage().getBody());
            //List<Map<String, Object>> vulnerabilities = ctx.read("$.vulnerabilityAlerts.nodes", List.class);

            int nodeCount = ctx.read("$.vulnerabilityAlerts.nodes.length()");
            for (int k = 0; k < nodeCount; k ++) {
                LOG.info(ctx.read("$.vulnerabilityAlerts.nodes[" + k + "]"));
            }


        /*

        TODO get rid of the query to the registry and use a loop in the route instead


         */

//        // java is sometimes a butt
//        // https://stackoverflow.com/questions/7283338/getting-an-element-from-a-set
//        PaginatedResponseBean paginatedResponseBean = exchange.getContext()
//                                                              .getRegistry()
//                                                              .findByType(PaginatedResponseBean.class)
//                                                              .stream()
//                                                              .findFirst()
//                                                              .get(); // we want this to blow up if the bean is missing
//
//        for (int i = 0; i < paginatedResponseBean.getNumberOfPaginatedResponses(); i++) {
//            // not worried about nulls because we know how many elements there are in the bean
//            String paginatedResponse = paginatedResponseBean.popPaginatedResponse().get();
//
//            ReadContext ctx = JsonPath.parse(paginatedResponse);
//            //List<Map<String, Object>> vulnerabilities = ctx.read("$.vulnerabilityAlerts.nodes", List.class);
//
//            int nodeCount = ctx.read("$.vulnerabilityAlerts.nodes.length()");
//            for (int k = 0; k < nodeCount; k ++) {
//                LOG.info(ctx.read("$.vulnerabilityAlerts.nodes[" + i + "]"));
//            }
//        }

    }

}
