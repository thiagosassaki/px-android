package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.PaymentMethod;
import java.util.List;

public class PaymentMethodsProviderImpl implements PaymentMethodsProvider {
    private final MercadoPagoServicesAdapter mercadoPago;

    public PaymentMethodsProviderImpl(@NonNull final Context context) throws IllegalStateException {
        mercadoPago = Session.getSession(context).getMercadoPagoServiceAdapter();
    }

    @Override
    public void getPaymentMethods(final TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
        mercadoPago.getPaymentMethods(resourcesRetrievedCallback);
    }
}
