package com.mercadopago.android.px.internal.features.hooks;

import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;

/**
 * Created by nfortuna on 10/25/17.
 */

public class HookProps {

    public final PaymentData paymentData;
    public final PaymentResult paymentResult;

    public HookProps(PaymentData paymentData, PaymentResult paymentResult) {
        this.paymentData = paymentData;
        this.paymentResult = paymentResult;
    }
}
