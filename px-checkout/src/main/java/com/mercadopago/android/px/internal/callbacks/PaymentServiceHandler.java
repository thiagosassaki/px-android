package com.mercadopago.android.px.internal.callbacks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.model.Card;

public interface PaymentServiceHandler extends PaymentProcessor.OnPaymentListener {

    /**
     * When flow is a saved card that does not have token saved
     * this method will be called to re-enter CVV and create the token again.
     */
    void onCvvRequired(@NonNull final Card card);

    /**
     * When payment processor has visual interaction this method will be called.
     */
    void onVisualPayment();

    /**
     * If payment was reject by invalid esc this method will be called.
     */
    void onRecoverPaymentEscInvalid();
}
