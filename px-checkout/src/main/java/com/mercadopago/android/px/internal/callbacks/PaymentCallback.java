package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.model.Payment;

/**
 * Created by vaserber on 1/19/17.
 */

public interface PaymentCallback extends ReturnCallback {
    void onSuccess(Payment payment);
}
