package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

/**
 * Created by mromar on 4/26/17.
 */

public interface IssuersProvider extends ResourcesProvider {

    void getIssuers(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback);

    MercadoPagoError getEmptyIssuersError();

    String getCardIssuersTitle();
}
