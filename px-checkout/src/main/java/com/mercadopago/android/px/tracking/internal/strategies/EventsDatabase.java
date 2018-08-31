package com.mercadopago.android.px.tracking.internal.strategies;

import com.mercadopago.android.px.model.Event;
import java.util.List;

public interface EventsDatabase {

    void persist(Event event);

    void returnEvents(List<Event> batch);

    Integer getBatchSize();

    List<Event> retrieveBatch();

    void clearExpiredTracks();

    Long getNextTrackTimestamp();

    void clearDatabase();
}
