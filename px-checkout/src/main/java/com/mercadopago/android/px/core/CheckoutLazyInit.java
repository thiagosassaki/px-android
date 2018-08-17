package com.mercadopago.android.px.core;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.di.Session;

@SuppressWarnings("unused")
public abstract class CheckoutLazyInit {

    private final MercadoPagoCheckout.Builder builder;
    private PrefetchService prefetchService;

    /**
     * CheckoutLazyInit allows you to prefetch {@link MercadoPagoCheckout} information.
     * Using this Lazy Builder you can avoid having a loading after call
     * {@link MercadoPagoCheckout#startPayment(Context, int)}
     *
     * @param builder Checkout builder to prefetch
     */
    protected CheckoutLazyInit(final MercadoPagoCheckout.Builder builder) {
        this.builder = builder;
    }

    /**
     * Starts fetch for {@link MercadoPagoCheckout}
     *
     * @param context your app context.
     */
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

    /**
     * If prefetch fails this method will be called
     *
     * @param mercadoPagoCheckout served, if you start it anyway it will fail or show a loading depending on the cause.
     */
    public abstract void fail(@NonNull final MercadoPagoCheckout mercadoPagoCheckout);

    /**
     * If prefetch is success this method will be called and will serve a {@link MercadoPagoCheckout} instance.
     *
     * @param mercadoPagoCheckout instance for you start the checkout process.
     */
    public abstract void success(@NonNull final MercadoPagoCheckout mercadoPagoCheckout);
}
