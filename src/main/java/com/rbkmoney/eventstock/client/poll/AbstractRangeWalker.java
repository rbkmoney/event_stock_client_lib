package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.eventstock.client.EventRange;

import java.util.function.Function;

/**
 * Created by vpankrashkin on 12.07.16.
 */
abstract  class AbstractRangeWalker<T> implements RangeWalker<T, EventRange<T>> {
    private final EventRange<T> range;
    private EventRange<T> walkingRange;
    private T walkingFromBound;

    public AbstractRangeWalker(EventRange<T> range) {
        this.range = range;
        this.walkingRange = range;
    }

    @Override
    public EventRange<T> getRange() {
        return range;
    }

    @Override
    public EventRange<T> getWalkingRange() {
        return walkingRange;
    }

    @Override
    public EventRange<T> setRange(Function<RangeWalker<T, EventRange<T>>, EventRange<T>> function) {
        walkingRange = function.apply(this);
        walkingFromBound = walkingRange.getFrom();
        return walkingRange;
    }

    @Override
    public EventRange moveRange(Function<RangeWalker<T, EventRange<T>>, T> function) {
        walkingFromBound = function.apply(this);
        walkingRange = createRange(walkingFromBound, range.getTo(), range);
        return walkingRange;
    }

    @Override
    public boolean isRangeOver() {
        return !range.accept(walkingRange.getFrom());
    }

    protected abstract EventRange<T> createRange(T fromBound, T toBound, EventRange<T> initialRange);
}
