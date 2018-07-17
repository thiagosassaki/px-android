package com.mercadopago.android.px.providers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.mvp.ResourcesProvider;
import java.util.List;

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
