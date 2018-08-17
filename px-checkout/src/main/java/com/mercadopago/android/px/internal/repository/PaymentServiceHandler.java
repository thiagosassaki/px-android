package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.model.Card;

public interface PaymentServiceHandler extends PaymentProcessor.OnPaymentListener {

    void onPaymentMethodRequired();

    void onCvvRequired(@NonNull final Card card);

    void onCardError();

    void onVisualPayment();

    void onIssuerRequired();

    void onPayerCostRequired();

    void onTokenRequired();
}
