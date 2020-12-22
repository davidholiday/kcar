package io.holitek;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.support.DefaultConsumer;

import java.util.concurrent.ExecutorService;

public class fooConsumer extends DefaultConsumer {
    private final fooEndpoint endpoint;
    private final EventBusHelper eventBusHelper;

    private ExecutorService executorService;

    public fooConsumer(fooEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        eventBusHelper = EventBusHelper.getInstance();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // start a single threaded pool to monitor events
        executorService = endpoint.createExecutor();

        // submit task to the thread pool
        executorService.submit(() -> {
            // subscribe to an event
            eventBusHelper.subscribe(this::onEventListener);
        });
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        // shutdown the thread pool gracefully
        getEndpoint().getCamelContext().getExecutorServiceManager().shutdownGraceful(executorService);
    }

    private void onEventListener(final Object event) {
        final Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody("Hello World! The time is " + event);

        try {
            // send message to next processor in the route
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}
