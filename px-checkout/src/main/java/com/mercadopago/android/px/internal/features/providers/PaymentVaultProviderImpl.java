package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.datasource.MercadoPagoESC;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.tracker.Tracker;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.tracking.internal.MPTracker;


public class PaymentVaultProviderImpl implements PaymentVaultProvider {

    private final Context context;

    private final MercadoPagoESC mercadoPagoESC;
    private final String merchantPublicKey;

    public PaymentVaultProviderImpl(final Context context) {
        this.context = context;
        final Session session = Session.getSession(context);
        mercadoPagoESC = session.getMercadoPagoESC();
        merchantPublicKey = session.getConfigurationModule().getPaymentSettings().getPublicKey();
    }

    @Override
    public String getTitle() {
        return context.getString(R.string.px_title_activity_payment_vault);
    }

    @Override
    public String getAllPaymentTypesExcludedErrorMessage() {
        return context.getString(R.string.px_error_message_excluded_all_payment_type);
    }

    @Override
    public String getInvalidDefaultInstallmentsErrorMessage() {
        return context.getString(R.string.px_error_message_invalid_default_installments);
    }

    @Override
    public String getInvalidMaxInstallmentsErrorMessage() {
        return context.getString(R.string.px_error_message_invalid_max_installments);
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.px_standard_error_message);
    }

    @Override
    public String getEmptyPaymentMethodsErrorMessage() {
        return context.getString(R.string.px_no_payment_methods_found);
    }

    public void initializeMPTracker(String siteId) {
        MPTracker.getInstance().initTracker(merchantPublicKey, siteId, BuildConfig.VERSION_NAME, context);
    }

    public void trackInitialScreen(PaymentMethodSearch paymentMethodSearch, String siteId) {
        initializeMPTracker(siteId);
        Tracker
            .trackPaymentVaultScreen(context, merchantPublicKey, paymentMethodSearch, mercadoPagoESC.getESCCardIds());
    }

    public void trackChildrenScreen(@NonNull final PaymentMethodSearchItem paymentMethodSearchItem,
        final @NonNull String siteId) {
        initializeMPTracker(siteId);
        Tracker.trackPaymentVaultChildrenScreen(context, merchantPublicKey, paymentMethodSearchItem);
    }
}
