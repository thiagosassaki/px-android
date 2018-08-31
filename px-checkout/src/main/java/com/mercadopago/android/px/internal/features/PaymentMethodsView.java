package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

/**
 * Created by mreverter on 1/5/17.
 */
public interface PaymentMethodsView extends MvpView {
    void showPaymentMethods(List<PaymentMethod> paymentMethods);

    void showProgress();

    void hideProgress();

    void showError(MercadoPagoError exception);

    void showBankDeals();
}
