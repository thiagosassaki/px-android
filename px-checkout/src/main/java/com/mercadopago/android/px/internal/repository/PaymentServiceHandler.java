package com.mercadopago.android.px.internal.repository;

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
     * If there is any card validation error this method will be called.
     */
    void onCardError();

    /**
     * If user has no payment method selected this method will be called.
     */
    void onPaymentMethodRequired();

    /**
     * If payment method is card but it does not have issuer yet, then this method
     * will be called.
     */
    void onIssuerRequired();

    /**
     * If payment method is card but it does not have payer cost yet, then this method
     * will be called.
     */
    void onPayerCostRequired();

    /**
     * If payment method is card but it does not have token yet, then this method
     * will be called.
     */
    void onTokenRequired();
}
