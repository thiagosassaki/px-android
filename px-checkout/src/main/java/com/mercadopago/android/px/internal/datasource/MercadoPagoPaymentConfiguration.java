package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.plugins.MercadoPagoPaymentProcessor;

public class MercadoPagoPaymentConfiguration extends PaymentConfiguration {

    public MercadoPagoPaymentConfiguration() {
        super(new MercadoPagoPaymentProcessor());
    }
}
