package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.damsel.event_stock.EventConstraint;
import com.rbkmoney.eventstock.client.*;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.domain.InvoicePaid;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.domain.InvoiceUnpaid;
import com.rbkmoney.damsel.event_stock.*;
import com.rbkmoney.damsel.event_stock.EventRange;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.condition.Relation;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;
import org.apache.thrift.TException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vpankrashkin on 29.06.16.
 */
public class TestClient extends AbstractTest {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);


    @Test
    public void test() throws URISyntaxException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
       addServlet(new THServiceBuilder().build(EventRepositorySrv.Iface.class, new EventRepositorySrv.Iface() {
           @Override
           public List<StockEvent> getEvents(EventConstraint constraint) throws InvalidRequest, DatasetTooBig, TException {
               log.info("Get Events: {}", constraint);
               EventIDRange idRange = constraint.getEventRange().getIdRange();
               long fromId = (Long) idRange.getFromId().getFieldValue();
               long toId = (Long) idRange.getToId().getFieldValue();
               int limit = constraint.getLimit();
               if (fromId >= toId) {
                   return Collections.emptyList();
               } else {
                   List list = new ArrayList();
                   for (long i = 0; i < limit && i+fromId <= toId; ++i) {
                        list.add(new StockEvent(SourceEvent.processing_event(createEvent(i+fromId))));
                   }
                   return list;
               }
           }

           private Event createEvent(long id) {
               String timeString =  TemporalConverter.temporalToString(LocalDateTime.now());
               Event event = id % 2 == 0 ? new Event(id, timeString,EventSource.invoice(""+id), 0, EventPayload.invoice_event(InvoiceEvent.invoice_status_changed(new InvoiceStatusChanged(InvoiceStatus.paid(new InvoicePaid())))))
                       :new Event(id, timeString,EventSource.invoice(""+id), 0, EventPayload.invoice_event(InvoiceEvent.invoice_status_changed(new InvoiceStatusChanged(InvoiceStatus.unpaid(new InvoiceUnpaid())))));
              return event;
           }

           @Override
           public StockEvent getLastEvent() throws NoLastEvent, TException {
               return null;
           }

           @Override
           public StockEvent getFirstEvent() throws NoStockEvent, TException {
               return null;
           }
       }), "/test");

        PollingEventPublisherBuilder eventPublisherBuilder = new PollingEventPublisherBuilder();
        eventPublisherBuilder.withEventHandler(new EventHandler() {
            @Override
            public void handleEvent(Object event, String subsKey) {
                log.info(subsKey+":Handled object: "+event);
            }

            @Override
            public void handleNoMoreElements(String subsKey) {
                log.info(subsKey+":No more elements");
                latch.countDown();
            }
        });
        eventPublisherBuilder.withURI(new URI(getUrlString("/test")));
        eventPublisherBuilder.withMaxQuerySize(2);

        PollingEventPublisher<StockEvent> eventPublisher = eventPublisherBuilder.build();

        eventPublisher.subscribe(createSubscriberConfig());


        latch.await();
        //eventPublisher.destroy();
    }

    private SubscriberConfig<StockEvent> createSubscriberConfig() {
        SubscriberConfig<StockEvent> config = new DefaultSubscriberConfig<StockEvent>() {
            @Override
            public EventFilter getEventFilter() {
                return  createEventFilter(0, 10);
            }
        };
      return config;
    }

    private EventFilter createEventFilter(long from, long to) {
        com.rbkmoney.eventstock.client.EventRange eventRange = new com.rbkmoney.eventstock.client.EventConstraint.EventIDRange();
        eventRange.setFromInclusive(from);
        eventRange.setToExclusive(to);
        Filter filter = new PathConditionFilter(new PathConditionRule("payload.invoice_event.invoice_status_changed.status", new com.rbkmoney.thrift.filter.condition.CompareCondition("unpaid", Relation.EQ)));
        EventFlowFilter eventFlowFilter = new EventFlowFilter(new com.rbkmoney.eventstock.client.EventConstraint(eventRange), filter);
        return eventFlowFilter;
    }
}
