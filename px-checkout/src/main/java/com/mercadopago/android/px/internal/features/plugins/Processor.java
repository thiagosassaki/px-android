package com.mercadopago.android.px.internal.features.plugins;

import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.GenericPayment;

public interface Processor {

    void process(BusinessPayment businessPayment);

    void process(GenericPayment pluginPaymentResult);
}
