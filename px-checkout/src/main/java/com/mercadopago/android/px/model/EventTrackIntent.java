package com.mercadopago.android.px.model;

import java.util.List;

public class EventTrackIntent {

    private final AppInformation application;
    private final DeviceInfo device;
    private final List<Event> events;

    public EventTrackIntent(final AppInformation application,
        final DeviceInfo device,
        final List<Event> events) {

        this.application = application;
        this.device = device;
        this.events = events;
    }

    public AppInformation getApplication() {
        return application;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public List<Event> getEvents() {
        return events;
    }
}
