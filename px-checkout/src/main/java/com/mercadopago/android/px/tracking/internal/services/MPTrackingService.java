package com.mercadopago.android.px.tracking.internal.services;

import com.mercadopago.android.px.model.EventTrackIntent;
import com.mercadopago.android.px.model.PaymentIntent;
import com.mercadopago.android.px.model.TrackingIntent;
import retrofit2.Callback;

/**
 * Created by vaserber on 7/3/17.
 */

public interface MPTrackingService {

    void trackToken(TrackingIntent trackingIntent);

    void trackPaymentId(PaymentIntent paymentIntent);

    void trackEvents(String publicKey, EventTrackIntent eventTrackIntent);

    void trackEvents(String publicKey, EventTrackIntent eventTrackIntent,
        Callback<Void> callback);
}
