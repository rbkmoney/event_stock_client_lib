package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventRange;

import java.time.Instant;

/**
 * Created by vpankrashkin on 12.07.16.
 */
class TimeRangeWalker extends AbstractRangeWalker<Instant> {

    public TimeRangeWalker(EventRange<Instant> range) {
        super(range);
    }

    @Override
    protected EventRange<Instant> createRange(Instant fromBound, Instant toBound, EventRange initialRange) {
        return new EventConstraint.EventTimeRange(fromBound, initialRange.isFromInclusive(), toBound, initialRange.isToInclusive());
    }
}
