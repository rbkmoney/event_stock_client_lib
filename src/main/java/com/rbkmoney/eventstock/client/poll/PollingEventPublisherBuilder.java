package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.damsel.event_stock.EventRepositorySrv;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.ErrorAction;
import com.rbkmoney.eventstock.client.ErrorHandler;
import com.rbkmoney.eventstock.client.EventAction;
import com.rbkmoney.eventstock.client.EventHandler;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Created by vpankrashkin on 29.06.16.
 */
public class PollingEventPublisherBuilder extends DefaultPollingEventPublisherBuilder {

    private URI uri;

    public URI getUri() {
        return uri;
    }

    public PollingEventPublisherBuilder withURI(URI uri) {
        this.uri = uri;
        return this;
    }

    protected THSpawnClientBuilder getClientBuilder() {
        THSpawnClientBuilder clientBuilder = new THSpawnClientBuilder().withAddress(uri);
        return clientBuilder;
    }

    protected ServiceAdapter createServiceAdapter() {
        THSpawnClientBuilder clientBuilder = getClientBuilder();
        return new ESServiceAdapter(clientBuilder.build(EventRepositorySrv.Iface.class));
    }

}
