package io.holitek.kcar.elements;


import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.Map;
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

    public static final String REPOSITORY_COUNT_HEADER_KEY = "repositoryCount";

    public static final String
            REPOSITORIES_WITH_VULNERABILITIES_COUNT_HEADER_KEY = "repositoriesWithVulnerabilitiesCount";

    public static final String VULNERABILITY_COUNT_HEADER_KEY = "vulnerabilityCount";

    public static final String CVSS_CRITICAL_COUNT_HEADER_KEY = "cvssCriticalCount";

    public static final String CVSS_HIGH_COUNT_HEADER_KEY = "cvssHighCount";

    public static final String CVSS_MEDIUM_COUNT_HEADER_KEY = "cvssMediumCount";

    public static final String CVSS_LOW_COUNT_HEADER_KEY = "cvssLowCount";

    public static final String CVSS_CRITICAL = "CRITICAL";

    public static final String CVSS_HIGH = "HIGH";

    public static final String CVSS_MEDIUM = "MODERATE";

    public static final String CVSS_LOW = "LOW";


    @Override
    public void process(Exchange exchange) throws Exception {
        // grab the paginated github response
        Optional<String> paginatedGithubResponseOptional = (Optional<String>)exchange.getMessage().getBody();
        if (paginatedGithubResponseOptional.isEmpty()) { throw new InvalidPayloadException(exchange, Optional.class); }
        String paginatedGithubResponse = paginatedGithubResponseOptional.get();
        LOG.debug(paginatedGithubResponse);

        // counts
        int vulnerabilityCount = 0;
        Map<String, Integer> cvssMap = Map.of( CVSS_CRITICAL, 0, CVSS_HIGH, 0, CVSS_MEDIUM, 0, CVSS_LOW, 0);

        ReadContext ctx = JsonPath.parse(paginatedGithubResponse);
        int repositoryCount = ctx.read("$.data.viewer.organization.repositories.nodes.length()");
        addToHeaderCounter(exchange, REPOSITORY_COUNT_HEADER_KEY, repositoryCount);

        for (int i = 0; i < repositoryCount; i ++) {
            int vulnerabilityAlertCount = ctx.read("$.data.viewer.organization.repositories.nodes.[" + i + "].vulnerabilityAlerts.nodes.length()");
            vulnerabilityCount += vulnerabilityAlertCount;
            for (int k = 0; k < vulnerabilityAlertCount; k ++) {
                String severity = ctx.read("$.data.viewer.organization.repositories.nodes.[" + i + "].vulnerabilityAlerts.nodes.[" + k + "].securityVulnerability.advisory.severity");
                int newSeverityCount = cvssMap.get(severity) + 1;
                cvssMap.put(severity, newSeverityCount);
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
