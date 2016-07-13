package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.eventstock.client.ErrorHandler;
import com.rbkmoney.eventstock.client.EventFilter;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.eventstock.client.SubscriberConfig;

/**
 * Created by vpankrashkin on 28.06.16.
 */
class PollingConfig<TEvent> implements SubscriberConfig<TEvent>{
    private final EventFilter<TEvent> eventFilter;
    private final EventHandler<TEvent> eventHandler;
    private final ErrorHandler errorHandler;
    private final int maxQuerySize;

    public PollingConfig(EventFilter<TEvent> eventFilter, EventHandler<TEvent> eventHandler, ErrorHandler errorHandler, int maxQuerySize) {
        this.eventFilter = eventFilter;
        this.eventHandler = eventHandler;
        this.errorHandler = errorHandler;
        this.maxQuerySize = maxQuerySize;
    }

    public PollingConfig(SubscriberConfig<TEvent> subscriberConfig) {
        this(subscriberConfig.getEventFilter(), subscriberConfig.getEventHandler(), subscriberConfig.getErrorHandler(), subscriberConfig.getMaxQuerySize());
    }

    public EventFilter<TEvent> getEventFilter() {
        return eventFilter;
    }

    public EventHandler<TEvent> getEventHandler() {
        return eventHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public int getMaxQuerySize() {
        return maxQuerySize;
    }

    public static <TEvent> PollingConfig<TEvent> mergeConfig(PollingConfig<TEvent> mainConfig, PollingConfig<TEvent> defaultConfig) {
        EventFilter<TEvent> eventFilter = mainConfig.getEventFilter() != null ? mainConfig.getEventFilter() : defaultConfig.getEventFilter();
        EventHandler<TEvent> eventHandler = mainConfig.getEventHandler() != null ? mainConfig.getEventHandler() : defaultConfig.getEventHandler();
        ErrorHandler errorHandler = mainConfig.getErrorHandler() != null ? mainConfig.getErrorHandler() : defaultConfig.getErrorHandler();
        int maxQuerySize = mainConfig.getMaxQuerySize() > 0 ? mainConfig.getMaxQuerySize() : defaultConfig.getMaxQuerySize();
        return new PollingConfig<>(eventFilter, eventHandler, errorHandler, maxQuerySize);
    }
}
