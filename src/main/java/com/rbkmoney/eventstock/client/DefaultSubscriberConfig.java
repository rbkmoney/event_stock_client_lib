package com.rbkmoney.eventstock.client;

/**
 * Created by vpankrashkin on 11.07.16.
 */
public class DefaultSubscriberConfig<TEvent> implements SubscriberConfig<TEvent> {
    @Override
    public EventFilter<TEvent> getEventFilter() {
        return null;
    }

    @Override
    public EventHandler<TEvent> getEventHandler() {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public int getMaxQuerySize() {
        return 0;
    }
}
