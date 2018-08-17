package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.PaymentMethod;
import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */

public interface PaymentMethodsProvider extends ResourcesProvider {
    void getPaymentMethods(TaggedCallback<List<PaymentMethod>> resourcesRetrievedCallback);
}
