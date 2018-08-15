package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.math.BigDecimal;
import java.util.List;

public interface InstallmentsActivityView extends MvpView {
    void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback);

    void finishWithResult(PayerCost payerCost);

    void showLoadingView();

    void hideLoadingView();

    void showError(MercadoPagoError error, String requestOrigin);

    void showHeader();

    void showInstallmentsRecyclerView();

    void warnAboutBankInterests();

    void showDetailDialog();

    void showDiscountInputDialog();

    void showAmount(@NonNull final DiscountRepository discountRepository,
        @NonNull final BigDecimal itemsPlusCharges,
        @NonNull final Site site);

    void onSuccessCodeDiscountCallback(Discount discount);

    void onFailureCodeDiscountCallback();
}
