package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;

/**
 * Created by vaserber on 8/17/17.
 */

public interface ReviewPaymentMethodsProvider extends ResourcesProvider {

    String getEmptyPaymentMethodsListError();

    String getStandardErrorMessage();
}
