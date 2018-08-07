package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.services.adapters.MPCall;
import com.mercadopago.android.px.viewmodel.OneTapModel;

public interface PaymentRepository {

    void doPayment(@NonNull final PaymentHandler paymentHandler);

    void doPayment(@NonNull final OneTapModel oneTapModel, @NonNull final PaymentHandler paymentHandler);

    @NonNull
    MPCall<Payment> createPaymentInMercadoPago();
}
