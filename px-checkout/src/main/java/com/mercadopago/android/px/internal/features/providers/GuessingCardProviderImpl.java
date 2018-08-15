package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Token;
import java.math.BigDecimal;
import java.util.List;

public class GuessingCardProviderImpl implements GuessingCardProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;
    private final String publicKey;
    private MPTrackingContext trackingContext;

    public GuessingCardProviderImpl(@NonNull final Context context) {
        this.context = context;
        final Session session = Session.getSession(context);
        publicKey = session.getConfigurationModule().getPaymentSettings().getPublicKey();
        mercadoPago = session.getMercadoPagoServiceAdapter();
    }

    @Override
    public MPTrackingContext getTrackingContext() {
        if (trackingContext == null) {
            trackingContext = new MPTrackingContext.Builder(context, publicKey)
                .setVersion(BuildConfig.VERSION_NAME)
                .build();
        }
        return trackingContext;
    }

    @Override
    public void getPaymentMethodsAsync(final TaggedCallback<List<PaymentMethod>> taggedCallback) {
        mercadoPago.getPaymentMethods(taggedCallback);
    }

    @Override
    public void createTokenAsync(CardToken cardToken, final TaggedCallback<Token> taggedCallback) {
        mercadoPago.createToken(cardToken, taggedCallback);
    }

    @Override
    public void getIssuersAsync(String paymentMethodId, String bin, final TaggedCallback<List<Issuer>> taggedCallback) {
        mercadoPago.getIssuers(paymentMethodId, bin, taggedCallback);
    }

    @Override
    public void getInstallmentsAsync(final String bin,
        final BigDecimal amount,
        final Long issuerId,
        final String paymentMethodId,
        @Nullable final Integer differentialPricingId,
        final TaggedCallback<List<Installment>> taggedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, differentialPricingId, taggedCallback);
    }

    @Override
    public void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback) {
        mercadoPago.getIdentificationTypes(taggedCallback);
    }

    @Override
    public void getBankDealsAsync(final TaggedCallback<List<BankDeal>> taggedCallback) {
        mercadoPago.getBankDeals(taggedCallback);
    }

    @Override
    public String getMissingInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.px_error_message_missing_installment_for_issuer);
    }

    @Override
    public String getMultipleInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.px_error_message_multiple_installments_for_issuer);
    }

    @Override
    public String getMissingPayerCostsErrorMessage() {
        return context.getString(R.string.px_error_message_missing_payer_cost);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.px_error_message_missing_identification_types);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.px_error_message_missing_public_key);
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.px_invalid_identification_number);
    }

    @Override
    public String getInvalidExpiryDateErrorMessage() {
        return context.getString(R.string.px_invalid_expiry_date);
    }

    @Override
    public String getInvalidEmptyNameErrorMessage() {
        return context.getString(R.string.px_invalid_empty_name);
    }

    @Override
    public String getSettingNotFoundForBinErrorMessage() {
        return context.getString(R.string.px_error_message_missing_setting_for_bin);
    }

    @Override
    public String getInvalidFieldErrorMessage() {
        return context.getString(R.string.px_invalid_field);
    }
}
