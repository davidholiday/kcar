package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;

import io.holitek.kcar.elements.PaginatedResponseBean;


/**
 * grabs the paginated github responses stored in a state bean and translates them into jira ticket payloads
 */
public class GithubToJiraTransformer implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GithubToJiraTransformer.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(GithubToJiraTransformer.class.getSimpleName());

    @Override
    public void process(Exchange exchange) throws Exception {

        PaginatedResponseBean[] paginatedResponseBeanArray =
                (PaginatedResponseBean[]) exchange.getContext()
                                                  .getRegistry()
                                                  .findByType(PaginatedResponseBean.class)
                                                  .toArray();

        // blow up if there isn't exactly one element in the list
        assert paginatedResponseBeanArray.length == 1;

        PaginatedResponseBean paginatedResponseBean = paginatedResponseBeanArray[0];

        for (int i = 0; i < paginatedResponseBean.getNumberOfPaginatedResponses(); i++) {

        }



    }

}
