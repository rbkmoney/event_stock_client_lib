package com.rbkmoney.eventstock.client.poll;

import com.rbkmoney.damsel.event_stock.*;
import com.rbkmoney.damsel.event_stock.EventConstraint;
import com.rbkmoney.damsel.event_stock.EventRange;
import com.rbkmoney.damsel.payment_processing.NoLastEvent;

import java.util.Collection;

/**
 * Created by vpankrashkin on 29.06.16.
 */
class ESServiceAdapter implements ServiceAdapter<StockEvent, com.rbkmoney.eventstock.client.EventConstraint> {
    private final EventRepositorySrv.Iface repository;

    public ESServiceAdapter(EventRepositorySrv.Iface repository) {
        this.repository = repository;
    }

    @Override
    public Collection<StockEvent> getEventRange(com.rbkmoney.eventstock.client.EventConstraint srcConstraint, int limit) throws ServiceException {

        EventConstraint resConstraint = convertConstraint(srcConstraint, limit);
        try {
            return repository.getEvents(resConstraint);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public StockEvent getFirstEvent() throws ServiceException {
        try {
            return repository.getFirstEvent();
        } catch (NoLastEvent e) {
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public StockEvent getLastEvent() throws ServiceException {
        try {
            return repository.getLastEvent();
        } catch (NoLastEvent e) {
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private EventConstraint convertConstraint(com.rbkmoney.eventstock.client.EventConstraint scrConstraint, int limit) {
        EventRange resRange = new EventRange();
        if (scrConstraint.getIdRange() != null) {
            resRange.setIdRange(convertRange(scrConstraint.getIdRange()));
        } else if (scrConstraint.getTimeRange() != null) {
            resRange.setTimeRange(convertRange(scrConstraint.getTimeRange()));
        }
        return new EventConstraint(resRange, limit);
    }

    private EventIDRange convertRange(com.rbkmoney.eventstock.client.EventConstraint.EventIDRange srcIdRange) {
        EventIDRange resIdRange = new EventIDRange();

        if (srcIdRange.isFromDefined()) {
            resIdRange.setFromId(srcIdRange.isFromInclusive() ? EventIDBound.inclusive(srcIdRange.getFrom()) : EventIDBound.exclusive(srcIdRange.getFrom()));
        }
        if (srcIdRange.isToDefined()) {
            resIdRange.setToId(srcIdRange.isToInclusive() ? EventIDBound.inclusive(srcIdRange.getTo()) : EventIDBound.exclusive(srcIdRange.getTo()));
        }

        return resIdRange;
    }

    private EventTimeRange convertRange(com.rbkmoney.eventstock.client.EventConstraint.EventTimeRange srcTimeRange) {
        EventTimeRange resTimeRange = new EventTimeRange();

        if (srcTimeRange.isFromDefined()) {
            String timeStr = TemporalConverter.temporalToString(srcTimeRange.getFrom());
            resTimeRange.setFromTime(srcTimeRange.isFromInclusive() ? EventTimeBound.inclusive(timeStr) : EventTimeBound.exclusive(timeStr));
        }
        if (srcTimeRange.isToDefined()) {
            String timeStr = TemporalConverter.temporalToString(srcTimeRange.getTo());
            resTimeRange.setToTime(srcTimeRange.isToInclusive() ? EventTimeBound.inclusive(timeStr) : EventTimeBound.exclusive(timeStr));
        }

        return resTimeRange;
    }
}
