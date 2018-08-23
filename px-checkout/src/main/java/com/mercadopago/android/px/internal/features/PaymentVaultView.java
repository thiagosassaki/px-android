package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface PaymentVaultView extends MvpView {

    void startSavedCardFlow(Card card);

    void showPaymentMethodPluginActivity();

    void showSelectedItem(PaymentMethodSearchItem item);

    void showProgress();

    void hideProgress();

    void showCustomOptions(List<CustomSearchItem> customSearchItems,
        OnSelectedCallback<CustomSearchItem> customSearchItemOnSelectedCallback);

    void showPluginOptions(Collection<PaymentMethodPlugin> items, PaymentMethodPlugin.PluginPosition position);

    void showSearchItems(List<PaymentMethodSearchItem> searchItems,
        OnSelectedCallback<PaymentMethodSearchItem> paymentMethodSearchItemSelectionCallback);

    void showError(MercadoPagoError mercadoPagoError, String requestOrigin);

    void setTitle(String title);

    void startCardFlow(Boolean automaticallySelection);

    void startPaymentMethodsSelection(final PaymentPreference paymentPreference);

    void finishPaymentMethodSelection(PaymentMethod selectedPaymentMethod);

    void showAmount(@NonNull final DiscountRepository discountRepository,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Site site);

    void collectPayerInformation();

    void cleanPaymentMethodOptions();

    void showHook(final Hook hook, final int code);

    void showDetailDialog();

    void showDiscountInputDialog();

    void onSuccessCodeDiscountCallback(final Discount discount);

    void onFailureCodeDiscountCallback();
}