package com.mercadopago.android.px.tracking.mocks;

import com.mercadopago.android.px.model.EventTrackIntent;
import com.mercadopago.android.px.model.PaymentIntent;
import com.mercadopago.android.px.model.TrackingIntent;
import com.mercadopago.android.px.tracking.internal.services.MPTrackingService;
import retrofit2.Callback;

public class MPMockedTrackingService implements MPTrackingService {

    @Override
    public void trackToken(final TrackingIntent trackingIntent) {

    }

    @Override
    public void trackPaymentId(final PaymentIntent paymentIntent) {

    }

    @Override
    public void trackEvents(final String publicKey, final EventTrackIntent eventTrackIntent) {

    }

    @Override
    public void trackEvents(final String publicKey, final EventTrackIntent eventTrackIntent,
        final Callback<Void> callback) {

    }
}
