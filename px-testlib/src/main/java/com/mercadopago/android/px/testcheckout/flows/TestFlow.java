package com.mercadopago.android.px.testcheckout.flows;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoCheckout;

public class TestFlow {

    protected Context context;
    protected MercadoPagoCheckout checkout;

    /**
     * If you already started the checkout you can use an empty constructor
     */
    protected TestFlow() {
    }

    /**
     * If you want to run with an instrumented context
     * you can use this static method.
     *
     * @param mercadoPagoCheckout the checkout configuration.
     * @param context context that will start the checkout.
     */
    protected TestFlow(@NonNull final MercadoPagoCheckout mercadoPagoCheckout, @NonNull final Context context) {
        checkout = mercadoPagoCheckout;
        this.context = context;
    }

    protected void startCheckout() {
        if (checkout != null && context != null) {
            checkout.startPayment(context, 1);
        }
    }
}
