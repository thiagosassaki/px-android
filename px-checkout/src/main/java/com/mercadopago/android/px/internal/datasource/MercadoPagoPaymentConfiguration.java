package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.configuration.PaymentConfiguration;

public class MercadoPagoPaymentConfiguration extends PaymentConfiguration {

    public MercadoPagoPaymentConfiguration() {
        super(new MercadoPagoPaymentProcessor());
    }
}
