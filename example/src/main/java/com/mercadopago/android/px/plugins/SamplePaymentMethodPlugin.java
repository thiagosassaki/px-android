package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.plugins.model.PaymentMethodInfo;
import com.mercadopago.example.R;

public class SamplePaymentMethodPlugin extends PaymentMethodPlugin {

    public SamplePaymentMethodPlugin() {
        super("account_money");
    }

    @Override
    @NonNull
    public PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context) {
        return new PaymentMethodInfo(
            getId(),
            "Dinero en cuenta",
            R.drawable.px_sample,
            "Custom payment method"
        );
    }

    @Override
    public String displayOrder() {
        return PaymentMethodPlugin.POSIION_BOTTOM;
    }
}
