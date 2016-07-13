package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventRange;

/**
 * Created by vpankrashkin on 12.07.16.
 */
class IdRangeWalker extends AbstractRangeWalker<Long> {
    public IdRangeWalker(EventRange<Long> range) {
        super(range);
    }

    @Override
    protected EventRange<Long> createRange(Long fromBound, Long toBound, EventRange initialRange) {
        return new EventConstraint.EventIDRange(fromBound, initialRange.isFromInclusive(), toBound, initialRange.isToInclusive());
    }
}
