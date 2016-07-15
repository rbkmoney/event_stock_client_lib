package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.eventstock.client.*;

import java.util.UUID;

/**
 * Created by vpankrashkin on 28.06.16.
 */
class PollingEventPublisher<TEvent> implements EventPublisher<TEvent> {
    private final PollingConfig defaultConfig;
    private final Poller poller;

    public PollingEventPublisher(PollingConfig defaultConfig, Poller poller) {
        this.defaultConfig = defaultConfig;
        this.poller = poller;
    }

    @Override
    public String subscribe(SubscriberConfig<TEvent> subscriberConfig) {
        PollingConfig<TEvent> mainConfig;
        if (subscriberConfig instanceof PollingConfig) {
            mainConfig = (PollingConfig<TEvent>) subscriberConfig;
        } else {
            mainConfig = new PollingConfig<>(subscriberConfig);
        }

        PollingConfig resultConfig = PollingConfig.mergeConfig(mainConfig, defaultConfig);

        do {
            String subsKey = UUID.randomUUID().toString();
            if (poller.addPolling(subsKey, resultConfig)) {
                return subsKey;
            }
        } while (!Thread.currentThread().isInterrupted());
        throw new IllegalStateException("Thread interrupted");
    }

    @Override
    public boolean unsubscribe(String subsKey) {
        return poller.removePolling(subsKey);
    }

    @Override
    public void unsubscribeAll() {
        poller.removeAll();
    }

    @Override
    public void destroy() {
        unsubscribeAll();
        poller.destroy();
    }

}