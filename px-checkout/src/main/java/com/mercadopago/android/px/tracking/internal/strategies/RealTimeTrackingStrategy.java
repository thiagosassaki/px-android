package com.mercadopago.android.px.tracking.internal.strategies;

import android.content.Context;
import com.mercadopago.android.px.model.AppInformation;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.EventTrackIntent;
import com.mercadopago.android.px.tracking.internal.services.MPTrackingService;
import java.util.ArrayList;
import java.util.List;

public class RealTimeTrackingStrategy extends TrackingStrategy {
    private final MPTrackingService trackingService;

    public RealTimeTrackingStrategy(final MPTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Override
    public void trackEvent(final Event event, final Context context) {
        final List<Event> events = new ArrayList<>();
        events.add(event);
        //Adapt to service v2.
        final AppInformation appInformation = getAppInformation().copy();
        appInformation.setFlowId(event.getFlowId());
        final EventTrackIntent eventTrackIntent = new EventTrackIntent(appInformation, getDeviceInfo(), events);
        trackingService.trackEvents(getPublicKey(), eventTrackIntent);
    }

    @Override
    public boolean readsEventFromDB() {
        return false;
    }
}
