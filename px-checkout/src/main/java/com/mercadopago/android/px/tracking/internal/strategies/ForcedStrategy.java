package com.mercadopago.android.px.tracking.internal.strategies;

import android.content.Context;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.EventTrackIntent;
import com.mercadopago.android.px.tracking.internal.services.MPTrackingService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForcedStrategy extends TrackingStrategy {

    private final MPTrackingService trackingService;
    private final ConnectivityChecker connectivityChecker;

    public ForcedStrategy(EventsDatabase database, ConnectivityChecker connectivityChecker,
        MPTrackingService trackingService) {
        setDatabase(database);
        this.trackingService = trackingService;
        this.connectivityChecker = connectivityChecker;
    }

    @Override
    public void trackEvent(Event event, Context context) {
        performTrackAttempt(context);
    }

    private void performTrackAttempt(Context context) {
        if (shouldSendBatch()) {
            sendTracksBatch(context);
        }
    }

    private boolean shouldSendBatch() {
        return isConnectivityOk() && isDataAvailable();
    }

    private boolean isConnectivityOk() {
        return connectivityChecker.hasConnection();
    }

    private void sendTracksBatch(final Context context) {
        final List<Event> savedEvents = getDatabase().retrieveBatch();
        List<EventTrackIntent> intents = groupEventsByFlow(savedEvents);
        for (EventTrackIntent intent : intents) {
            trackingService.trackEvents(getPublicKey(), intent, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        performTrackAttempt(context);
                    } else {
                        getDatabase().returnEvents(savedEvents);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    getDatabase().returnEvents(savedEvents);
                }
            });
        }
    }
}
