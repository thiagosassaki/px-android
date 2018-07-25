package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.services.exceptions.ApiException;

public interface PaymentHandler {

    void onPaymentSuccess(@NonNull final Payment payment);

    void onPaymentError(@NonNull final ApiException error);

    void onPaymentMethodRequired();

    void onCvvRequired(@NonNull final Card card);

    void onCardError();
}
