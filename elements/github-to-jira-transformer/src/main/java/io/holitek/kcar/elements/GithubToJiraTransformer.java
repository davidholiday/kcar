package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;

import org.apache.hc.client5.http.fluent.Request;
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

    public static final String REPO_COUNT_HEADER_KEY = "repoCount";

    public static final String REPO_WITH_ALERTS_ENABLED_COUNT_HEADER_KEY = "repoWithAlertsEnabled";

    public static final String REPO_WITH_VULNERABILITIES_COUNT_HEADER_KEY = "repoWithVulnerabilitiesCount";

    public static final String VULNERABILITY_ALERT_COUNT_HEADER_KEY = "vulnerabilityAlertCount";


    @Override
    public void process(Exchange exchange) throws Exception {
        // grab the paginated github response
        Optional<String> paginatedGithubResponseOptional = (Optional<String>)exchange.getMessage().getBody();
        if (paginatedGithubResponseOptional.isEmpty()) { throw new InvalidPayloadException(exchange, Optional.class); }
        String paginatedGithubResponse = paginatedGithubResponseOptional.get();
        LOG.debug(paginatedGithubResponse);

        ReadContext ctx = JsonPath.parse(paginatedGithubResponse);
        int repositoryCount = ctx.read("$.data.viewer.organization.repositories.nodes.length()");
        addToHeaderCounter(exchange, REPO_COUNT_HEADER_KEY, repositoryCount);

        for (int i = 0; i < repositoryCount; i ++) {
            // there doesn't seem to be a graphql way to do this :-(
            //https://docs.github.com/en/rest/reference/repos#check-if-vulnerability-alerts-are-enabled-for-a-repository
            String repositoryName = ctx.read("$.data.viewer.organization.repositories.nodes.[" + i + "].name");
            String accessToken = exchange.getContext().getPropertiesComponent().resolveProperty("env.GITHUB_ACCESS_TOKEN").get();

            int rc = Request.get("https://api.github.com/repos/life360/" + repositoryName + "/vulnerability-alerts")
                            .setHeader("Authorization", accessToken)
                            .setHeader("Accept", "Accept: application/vnd.github.dorian-preview+json")
                            .execute()
                            .returnResponse()
                            .getCode();

            // on 204 update counter
            // on 404 do nothing
            if (rc == 204) { addToHeaderCounter(exchange, REPO_WITH_ALERTS_ENABLED_COUNT_HEADER_KEY, 1); }


            int vulnerabilityAlertCount = ctx.read("$.data.viewer.organization.repositories.nodes.[" + i + "].vulnerabilityAlerts.nodes.length()");
            if (vulnerabilityAlertCount > 0) {
                addToHeaderCounter(exchange, REPO_WITH_VULNERABILITIES_COUNT_HEADER_KEY, 1);
            }
            addToHeaderCounter(exchange, VULNERABILITY_ALERT_COUNT_HEADER_KEY, vulnerabilityAlertCount);
            for (int k = 0; k < vulnerabilityAlertCount; k ++) {
                String severity = ctx.read("$.data.viewer.organization.repositories.nodes.[" + i + "].vulnerabilityAlerts.nodes.[" + k + "].securityVulnerability.advisory.severity");
                addToHeaderCounter(exchange, severity, 1);
            }
        }

    }


    /**
     * for updating the counts contained in the message header. handles case where header value doesn't exist yet.
     *
     * @param exchange
     * @param headerName
     * @param summand
     */
    private void addToHeaderCounter(Exchange exchange, String headerName, int summand) {
        Integer currentValue = (Integer)exchange.getMessage().getHeader(headerName, 0);
        Integer newValue = currentValue + summand;
        exchange.getMessage().setHeader(headerName, newValue);
    }

}
