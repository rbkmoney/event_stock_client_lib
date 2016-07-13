package com.rbkmoney.eventstock.client;

/**
 * Created by vpankrashkin on 07.07.16.
 *
 * Represents range of values. Range is forward oriented and doesn't support rewinding.
 *
 */
public abstract class EventRange<T> {
    private T from;
    private T to;
    private boolean fromNowFlag;
    private boolean fromInclusiveFlag;
    private boolean toInclusiveFlag;

    public EventRange(T from, boolean fromInclusiveFlag, T to, boolean toInclusiveFlag) {
        if (from == null && to == null) {
            throw new NullPointerException("Both bounds cannot be null");
        }
        this.from = from;
        this.to = to;
        this.fromInclusiveFlag = fromInclusiveFlag;
        this.toInclusiveFlag = toInclusiveFlag;
    }

    public EventRange() {
    }

    /**
     * Set the down bound value.
     * If value is null, range is down-bounded with first existing event.
     * */
    public void setFrom(T val, boolean inclusive) {
        this.from = val;
        this.fromInclusiveFlag = inclusive;
        this.fromNowFlag = false;
    }

    /**
     * Set the down bound to last existing event, if no such event found, it'll be waited for.
     * */
    public void setFromNow() {
        setFrom(null, true);
        this.fromNowFlag = true;
    }

    public void setFromValue(T val) {
        setFrom(val, isFromInclusive());
    }

    public void setToValue(T val) {
        setTo(val, isToInclusive());
    }

    /**
     * Set the up bound value.
     * If value is null, range is not up-bounded.
     * */
    public void setTo(T val, boolean inclusive) {
        this.to = val;
        this.toInclusiveFlag = inclusive;
    }


    public void setFromInclusive(T val) {
        setFrom(val, true);
    }

    public void setFromExclusive(T val) {
        setFrom(val, false);
    }

    public void setToInclusive(T val) {
        setTo(val, true);
    }

    public void setToExclusive(T val) {
        setTo(val, false);
    }


    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    public boolean isFromInclusive() {
        return fromInclusiveFlag;
    }

    public boolean isToInclusive() {
        return toInclusiveFlag;
    }

    public boolean isFromNow() {
        return fromNowFlag;
    }

    public boolean isFromDefined() {
        return from != null;
    }

    public boolean isToDefined() {
        return to != null;
    }

    /**
     * @return true - if range has both up and down ranges; false - otherwise.
     * */
    public boolean isDefined() {
        return isFromDefined() && isToDefined();
    }

    public abstract boolean accept(T val);
}
