package com.mercadopago.android.px.core;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.di.Session;

@SuppressWarnings("unused")
public abstract class CheckoutLazyBuilder {

    private final MercadoPagoCheckout.Builder builder;
    private PrefetchService prefetchService;

    protected CheckoutLazyBuilder(final MercadoPagoCheckout.Builder builder) {
        this.builder = builder;
    }

    public final void fetch(final Context context) {
        final MercadoPagoCheckout checkout = builder.build();
        // Generates new session.
        prefetchService = new PrefetchService(checkout, Session.getSession(context), this);
        prefetchService.prefetch();
    }

    public final void fail() {
        // Generates new session.
        fail(builder.build());
    }

    public void cancel() {
        if (prefetchService != null) {
            prefetchService.cancel();
        }
    }

    public abstract void fail(@NonNull final MercadoPagoCheckout mercadoPagoCheckout);

    public abstract void success(@NonNull final MercadoPagoCheckout mercadoPagoCheckout);
}
