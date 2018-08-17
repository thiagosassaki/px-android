package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.IdentificationType;
import java.util.List;

public class PayerInformationProviderImpl implements PayerInformationProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public PayerInformationProviderImpl(@NonNull final Context context) {
        this.context = context;
        mercadoPago = Session.getSession(context).getMercadoPagoServiceAdapter();
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.px_invalid_identification_number);
    }

    @Override
    public String getInvalidIdentificationNameErrorMessage() {
        return context.getString(R.string.px_invalid_identification_name);
    }

    @Override
    public String getInvalidIdentificationLastNameErrorMessage() {
        return context.getString(R.string.px_invalid_identification_last_name);
    }

    @Override
    public String getInvalidIdentificationBusinessNameErrorMessage() {
        return context.getString(R.string.px_invalid_identification_last_name);
    }

    @Override
    public void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback) {
        mercadoPago.getIdentificationTypes(taggedCallback);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.px_error_message_missing_public_key);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.px_error_message_missing_identification_types);
    }
}
