package com.mercadopago.android.px.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.mvp.TaggedCallback;
import java.util.List;

public class PaymentMethodsProviderImpl implements PaymentMethodsProvider {
    private final MercadoPagoServicesAdapter mercadoPago;

    public PaymentMethodsProviderImpl(@NonNull final Context context) throws IllegalStateException {
        final String publicKey =
            Session.getSession(context).getConfigurationModule().getPaymentSettings().getPublicKey();
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey);
    }

    @Override
    public void getPaymentMethods(final TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback) {
        mercadoPago.getPaymentMethods(resourcesRetrievedCallback);
    }
}
