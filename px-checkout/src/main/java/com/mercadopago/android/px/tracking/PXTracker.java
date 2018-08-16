package com.mercadopago.android.px.tracking;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.MPTracker;

@SuppressWarnings("unused")
public final class PXTracker {

    private PXTracker() {
    }

    /**
     * Set your own tracker listener to be aware of PX - Checkout events.
     *
     * @param listener your listener.
     */
    public static void setListener(@NonNull final PXEventListener listener) {
        MPTracker.getInstance().setTracksListener(listener);
    }
}
