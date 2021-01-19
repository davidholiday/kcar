package io.holitek.kcar.elements;


import io.holitek.kcar.helpers.CamelPropertyHelper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.Optional;
import java.util.UUID;


/**
 * a processor that will kick off kick off a background job. what gets kicked off, and with what parameters, is
 * determined by properties. Will set the body of the Message to the jobID assigned the background process.
 */
public class StartAsyncJobProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(StartAsyncJobProcessor.class);

    // this will let us treat the producerTemplate as a singleton. we can't create this until we have an exchange
    private Optional<ProducerTemplate> producerTemplateOptional = Optional.empty();

    // this will let us resolve the background job route property once and only once
    private Optional<String> backgroundJobRoutePropertyOptional = Optional.empty();

    // this will let us resolve the background job body property once and only once
    private Optional <String> backgroundJobBodyPropertyOptional = Optional.empty();

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(StartAsyncJobProcessor.class.getSimpleName());

    // property key to fetch the URI of the route to kick off in the background
    public static final String BACKGROUND_JOB_ROUTE_PROPERTY_KEY =
            CamelPropertyHelper.getPropertyKey(NAMESPACE_KEY, "backgroundJobRoute");

    // property key to fetch what to send to the background job.
    public static final String BACKGROUND_JOB_BODY_PROPERTY_KEY =
            CamelPropertyHelper.getPropertyKey(NAMESPACE_KEY, "backgroundJobBody");


    @Override
    public void process(Exchange exchange) throws Exception {

        // guard against empty optionals
        //
        if (producerTemplateOptional.isEmpty()) {
            ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
            producerTemplateOptional = Optional.of(producerTemplate);
        }

        if (backgroundJobRoutePropertyOptional.isEmpty()) {
            String backgroundJobRoute = exchange.getContext()
                                                .getPropertiesComponent()
                                                .resolveProperty(BACKGROUND_JOB_ROUTE_PROPERTY_KEY)
                                                .orElseThrow();

            backgroundJobRoutePropertyOptional = Optional.of(backgroundJobRoute);
        }

        if (backgroundJobBodyPropertyOptional.isEmpty()) {
            String backgroundJobBody = exchange.getContext()
                                               .getPropertiesComponent()
                                               .resolveProperty(BACKGROUND_JOB_BODY_PROPERTY_KEY)
                                               .orElseThrow();

            backgroundJobBodyPropertyOptional = Optional.of(backgroundJobBody);
        }


        // the producer template async methods return a "CompletableFuture" object, which has a hook "whenComplete" that
        // takes a lambda that's compatible with the bi-consumer interface. the endpoint to kick off, the body to send,
        // and what to do after is set by config values.
        //
        // see:
        // https://camel.apache.org/manual/latest/producertemplate.html
        // https://www.javadoc.io/doc/org.apache.camel/camel-api/latest/org/apache/camel/ProducerTemplate.html
        // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html?is-external=true
        // https://mincong.io/2020/05/30/exception-handling-in-completable-future/
        // https://javabydeveloper.com/java-biconsumer-guide-examples/
        //
        ProducerTemplate producerTemplate = producerTemplateOptional.get();
        String backgroundJobRoute = backgroundJobRoutePropertyOptional.get();
        String backgroundJobBody = backgroundJobBodyPropertyOptional.get();
        String jobID = UUID.randomUUID().toString();

        LOG.info("* kicking off background process * jobID is: {}. sending body: {} to endpoint: {}",
                 jobID, backgroundJobBody, backgroundJobRoute
        );
        producerTemplate.asyncSendBody(backgroundJobRoute, backgroundJobBody)
                        .whenComplete((msg, ex) -> {
                            if (ex != null) {
                                LOG.error("something went wrong with background process {}!", jobID);
                            } else {
                                LOG.info("jobID: {} completed successfully!", jobID);
                            }

                        });


        // make sure callers have a means of checking in on the job that got started
        //
        exchange.getMessage()
                .setBody(jobID);
    }

}
