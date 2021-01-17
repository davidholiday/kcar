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
 * determined by properties.
 */
public class StartAsyncJobProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(StartAsyncJobProcessor.class);

    // anything to do with this element - from properties to identification - will use this top level key
    public static final String NAMESPACE_KEY = Introspector.decapitalize(StartAsyncJobProcessor.class.getSimpleName());

    // this will let us treat the producerTemplate as a singleton
    private Optional<ProducerTemplate> producerTemplateOptional = Optional.empty();


    public static final String HEALTH_CHECK_ROUTE_ENTRYPOINT =
            CamelPropertyHelper.getPropertyPlaceholder(NAMESPACE_KEY, "entryPoint");


    @Override
    public void process(Exchange exchange) throws Exception {

        // guard against empty optional
        if (producerTemplateOptional.isEmpty()) {
            ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
            producerTemplateOptional = Optional.of(producerTemplate);
        }

        // the producer template async methods return a "CompletableFuture" object, which has a hook "whenComplete" that
        // takes a lambda that's compatible with the bi-consumer interface. the endpoint to kick off, the body to send,
        // and what to do after is set by config values
        //
        // see:
        // https://camel.apache.org/manual/latest/producertemplate.html
        // https://www.javadoc.io/doc/org.apache.camel/camel-api/latest/org/apache/camel/ProducerTemplate.html
        // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html?is-external=true
        // https://mincong.io/2020/05/30/exception-handling-in-completable-future/
        // https://javabydeveloper.com/java-biconsumer-guide-examples/
        ProducerTemplate producerTemplate = producerTemplateOptional.get();
        String jobID = UUID.randomUUID().toString();
        LOG.info("* kicking off background process * jobID is: {}. sending body: {} to endpoint: {}",
                 "jobID", "fart", "endpointURI"
        );
        producerTemplate.asyncSendBody("endpointURI", "fart")
                        .whenComplete((msg, ex) -> {
                            if (ex != null) {
                                LOG.error("something went wrong with background process {}!", jobID);
                            } else {
                                LOG.info("jobID: {} completed successfully!", jobID);
                            }

                        });

    }

}
