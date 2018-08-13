package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.plugins.MercadoPagoPaymentProcessor;
import com.mercadopago.android.px.preferences.PaymentConfiguration;

public class MercadoPagoPaymentConfiguration extends PaymentConfiguration {

    public MercadoPagoPaymentConfiguration() {
        super(new MercadoPagoPaymentProcessor());
    }
}
