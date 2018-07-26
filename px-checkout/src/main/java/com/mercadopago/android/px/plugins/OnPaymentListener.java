package com.mercadopago.android.px.plugins;

import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.plugins.model.PluginPayment;

public interface OnPaymentListener {
    void onPaymentFinished(PluginPayment payment);

    void onPaymentError(MercadoPagoError error);
}
