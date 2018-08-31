package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.model.PaymentData;

/**
 * Created by vaserber on 1/19/17.
 */

public interface PaymentDataCallback extends ReturnCallback {
    void onSuccess(PaymentData paymentData, boolean paymentMethodChanged);
}
