package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.math.BigDecimal;
import java.util.List;

public class InstallmentsProviderImpl implements InstallmentsProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;

    public InstallmentsProviderImpl(@NonNull final Context context) {
        this.context = context;
        mercadoPago = Session.getSession(context).getMercadoPagoServiceAdapter();
    }

    @Override
    public void getInstallments(final String bin,
        final BigDecimal amount,
        final Long issuerId,
        final String paymentMethodId,
        @Nullable final Integer differentialPricingId,
        final TaggedCallback<List<Installment>> taggedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, differentialPricingId, taggedCallback);
    }

    @Override
    public MercadoPagoError getNoInstallmentsFoundError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.px_error_message_detail_no_installments);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.px_error_message_detail_multiple_installments);

        return new MercadoPagoError(message, detail, false);
    }

    @Override
    public MercadoPagoError getNoPayerCostFoundError() {
        String message = getStandardErrorMessage();
        String detail = context.getString(R.string.px_error_message_detail_no_payer_cost_found);

        return new MercadoPagoError(message, detail, false);
    }

    public String getStandardErrorMessage() {
        return context.getString(R.string.px_standard_error_message);
    }
}
