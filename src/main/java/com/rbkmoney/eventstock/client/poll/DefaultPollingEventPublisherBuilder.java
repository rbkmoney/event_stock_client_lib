package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.ErrorAction;
import com.rbkmoney.eventstock.client.ErrorHandler;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vpankrashkin on 29.06.16.
 */
public class DefaultPollingEventPublisherBuilder {
    protected static final EventHandler DEFAULT_EVENT_HANDLER =   new EventHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public EventAction handle(Object event, String subsKey) {
            log.trace("Subscription: {}, new event: {}", subsKey, event);
            return EventAction.CONTINUE;
        }

        @Override
        public void handleCompleted(String subsKey) {
            log.debug("Subscription completed: {}", subsKey);
        }

        @Override
        public void handleInterrupted(String subsKey) {
            log.debug("Subscription interrupted: {}", subsKey);
        }
    };

    protected static final ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler() {
        private  final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public ErrorAction handleError(String subsKey, Throwable errCause) {
            log.error("Subscription error: " + subsKey, errCause);
            return ErrorAction.INTERRUPT;
        }
    };

    protected static final int DEFAULT_MAX_QUERY_SIZE = 100;
    protected static final int DEFAULT_MAX_POOL_SIZE = -1;
    protected static final int DEFAULT_MAX_POLL_DELAY = 1000;

    private EventHandler eventHandler;
    private ErrorHandler errorHandler;
    private ServiceAdapter serviceAdapter;
    private int maxQuerySize = DEFAULT_MAX_QUERY_SIZE;
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private int pollDelay = DEFAULT_MAX_POLL_DELAY;

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public int getMaxQuerySize() {
        return maxQuerySize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getPollDelay() {
        return pollDelay;
    }

    public DefaultPollingEventPublisherBuilder withEventHandler(EventHandler eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("Null event handler");
        }
        this.eventHandler = eventHandler;
        return this;
    }

    public DefaultPollingEventPublisherBuilder withErrorHandler(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("Null event handler");
        }
        this.errorHandler = errorHandler;
        return this;
    }

    public DefaultPollingEventPublisherBuilder withMaxQuerySize(int maxQuerySize) {
        if (maxQuerySize <= 0) {
            throw new IllegalArgumentException("Max query size must be > 0");
        }
        this.maxQuerySize = maxQuerySize;
        return this;
    }

    public DefaultPollingEventPublisherBuilder withMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public DefaultPollingEventPublisherBuilder withPollDelay(int pollingDelayMs) {
        if (pollingDelayMs < 0) {
            throw new IllegalArgumentException("Poll delay must be > 0");
        }
        this.pollDelay = pollingDelayMs;
        return this;
    }

    public DefaultPollingEventPublisherBuilder withServiceAdapter(ServiceAdapter serviceAdapter) {
        if (serviceAdapter == null) {
            throw new IllegalArgumentException("ServiceAdapter cannot be null");
        }
        this.serviceAdapter = serviceAdapter;
        return this;
    }

    public ServiceAdapter getServiceAdapter() {
        return serviceAdapter;
    }

    protected ServiceAdapter createServiceAdapter() {
        throw new UnsupportedOperationException("Cannot create ServiceAdapter");
    }

    public PollingEventPublisher<StockEvent> build() {
        ServiceAdapter adapter = getServiceAdapter();
        adapter = adapter == null ? createServiceAdapter() : serviceAdapter;
        Poller poller = new Poller(adapter, getMaxPoolSize(), getPollDelay());
        EventHandler eventHandler = getEventHandler();
        eventHandler = eventHandler == null ? DEFAULT_EVENT_HANDLER : eventHandler;
        ErrorHandler errorHandler = getErrorHandler();
        errorHandler = errorHandler == null ? DEFAULT_ERROR_HANDLER : errorHandler;
        PollingConfig<StockEvent> pollingConfig = new PollingConfig<>(eventHandler, errorHandler, getMaxQuerySize());
        PollingEventPublisher eventPublisher = new PollingEventPublisher(pollingConfig, poller);
        return eventPublisher;
    }
}
