package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.woody.api.ClientBuilder;

public class XratesPollingEventPublisherBuilder extends AbstractPollingEventPublisherBuilder<XratesPollingEventPublisherBuilder> {

    @Override
    protected ServiceAdapter createServiceAdapter(ClientBuilder clientBuilder) {
        return XratesServiceAdapter.build(clientBuilder);
    }
}
