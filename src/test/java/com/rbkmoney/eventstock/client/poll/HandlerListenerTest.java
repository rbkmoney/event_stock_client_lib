package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.*;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

/**
 * Created by vpankrashkin on 09.06.17.
 */
public class HandlerListenerTest {
    @Test
    public void testHandling() throws InterruptedException {
        final long testCount = 1000000;
        final AtomicBoolean interrupted = new AtomicBoolean();
        final AtomicInteger prcCount = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(1);
        PollingEventPublisher<StockEvent> publisher = new DefaultPollingEventPublisherBuilder()
                .withEventHandler(
                        new EventHandler<StockEvent>() {
                            @Override
                            public EventAction handle(StockEvent event, String subsKey) {
                                prcCount.incrementAndGet();
                                return EventAction.CONTINUE;
                            }

                            @Override
                            public void handleCompleted(String subsKey) {
                                interrupted.set(false);
                                latch.countDown();
                            }

                            @Override
                            public void handleInterrupted(String subsKey) {
                                interrupted.set(true);
                                latch.countDown();
                            }
                        })
                .withErrorHandler((subsKey, errCause) -> {
                    errCause.printStackTrace();
                    return ErrorAction.INTERRUPT;
                })
                .withServiceAdapter(new EventGenerator.ServiceAdapterStub())
                .withMaxQuerySize(1000)
                .build();
        long startTime = System.currentTimeMillis();
        publisher.subscribe(new DefaultSubscriberConfig<>(
                new EventFlowFilter(
                        new EventConstraint(
                                new EventConstraint.EventIDRange(0L, testCount)))
        ));

        latch.await();
        out.printf("Count: %d, Time: %d\n", testCount, (System.currentTimeMillis() - startTime));
    }

}
