package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.model.PaymentResult;

/**
 * Created by vaserber on 2/21/17.
 */

public interface PaymentResultCallback {
    void onResult(PaymentResult paymentResult);
}
