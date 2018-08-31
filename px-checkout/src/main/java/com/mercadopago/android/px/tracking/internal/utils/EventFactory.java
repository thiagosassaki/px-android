package com.mercadopago.android.px.tracking.internal.utils;

import com.mercadopago.android.px.model.ActionEvent;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.ScreenViewEvent;

public class EventFactory {

    private static String NO_TRACKING_SUPPORTED_ERROR_MESSAGE = "Tracking not supported for that event type";

    public static Event getEvent(String toParseJson) {
        Event event = null;
        if (toParseJson.contains(Event.TYPE_SCREEN_VIEW)) {
            event = JsonConverter.getInstance().fromJson(toParseJson, ScreenViewEvent.class);
        } else if (toParseJson.contains(Event.TYPE_ACTION)) {
            event = JsonConverter.getInstance().fromJson(toParseJson, ActionEvent.class);
        } else {
            throw new IllegalStateException(NO_TRACKING_SUPPORTED_ERROR_MESSAGE);
        }

        return event;
    }
}
