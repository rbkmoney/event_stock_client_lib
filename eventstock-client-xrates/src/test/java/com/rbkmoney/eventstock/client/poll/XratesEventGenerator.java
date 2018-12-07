package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.xrates.base.Rational;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

public class XratesEventGenerator {

    public static SinkEvent createXratesEvent(long id) {
        String timeString = TypeUtil.temporalToString(Instant.now());
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setId(id);
        sinkEvent.setCreatedAt(timeString);
        sinkEvent.setPayload(
                new Event(
                        Arrays.asList(
                                Change.created(
                                        new ExchangeRateCreated(
                                                new ExchangeRateData(
                                                        new TimestampInterval("1", "2"),
                                                        Arrays.asList(
                                                                new Quote(
                                                                        new Currency("1", (short) 2),
                                                                        new Currency("1", (short) 2),
                                                                        new Rational(1, 2)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                )
        );
        try {
            sinkEvent = new MockTBaseProcessor(MockMode.REQUIRED_ONLY).process(sinkEvent, new TBaseHandler<>(SinkEvent.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sinkEvent;
    }
}
