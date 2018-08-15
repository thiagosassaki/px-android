package com.mercadopago.android.px.internal.features.providers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;

public interface PaymentVaultProvider extends ResourcesProvider {
    String getTitle();

    String getAllPaymentTypesExcludedErrorMessage();

    String getInvalidDefaultInstallmentsErrorMessage();

    String getInvalidMaxInstallmentsErrorMessage();

    String getStandardErrorMessage();

    String getEmptyPaymentMethodsErrorMessage();

    void trackInitialScreen(PaymentMethodSearch paymentMethodSearch, String siteId);

    void trackChildrenScreen(@NonNull PaymentMethodSearchItem paymentMethodSearchItem, @NonNull String siteId);

}
