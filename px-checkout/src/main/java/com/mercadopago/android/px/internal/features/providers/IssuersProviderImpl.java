package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

/**
 * Created by mromar on 4/26/17.
 */

public class IssuersProviderImpl implements IssuersProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public IssuersProviderImpl(@NonNull final Context context) {
        this.context = context;
        mercadoPago = Session.getSession(context).getMercadoPagoServiceAdapter();
    }

    @Override
    public void getIssuers(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback) {
        mercadoPago.getIssuers(paymentMethodId, bin, taggedCallback);
    }

    @Override
    public MercadoPagoError getEmptyIssuersError() {
        String message = context.getString(R.string.px_standard_error_message);
        String detail = context.getString(R.string.px_error_message_detail_issuers);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public String getCardIssuersTitle() {
        return context.getString(R.string.px_card_issuers_title);
    }
}
