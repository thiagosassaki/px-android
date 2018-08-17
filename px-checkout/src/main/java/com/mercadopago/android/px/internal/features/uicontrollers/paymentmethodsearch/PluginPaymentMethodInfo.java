package com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethodInfo;

public class PluginPaymentMethodInfo implements PaymentMethodInfoModel {

    private PaymentMethodInfo paymentMethodInfo;

    public PluginPaymentMethodInfo(@NonNull PaymentMethodInfo paymentMethodInfo) {
        this.paymentMethodInfo = paymentMethodInfo;
    }

    @Override
    public String getId() {
        return paymentMethodInfo.getId();
    }

    @Override
    public String getName() {
        return paymentMethodInfo.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getIcon() {
        return paymentMethodInfo.getIcon();
    }
}
