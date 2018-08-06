package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.plugins.OnPaymentListener;

public interface PaymentHandler extends OnPaymentListener {

    void onPaymentMethodRequired();

    void onCvvRequired(@NonNull final Card card);

    void onCardError();

    void onVisualPayment(Fragment fragment);

    void onIssuerRequired();

    void onPayerCostRequired();

    void onTokenRequired();
}
