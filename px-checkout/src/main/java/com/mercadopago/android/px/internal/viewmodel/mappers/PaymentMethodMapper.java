package com.mercadopago.android.px.internal.viewmodel.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;

public class PaymentMethodMapper extends Mapper<PaymentMethodSearch, PaymentMethod> {

    @Override
    public PaymentMethod map(@NonNull final PaymentMethodSearch val) {
        final String paymentMethodId = val.getOneTapMetadata().getPaymentMethodId();
        return val.getPaymentMethodById(paymentMethodId);
    }
}