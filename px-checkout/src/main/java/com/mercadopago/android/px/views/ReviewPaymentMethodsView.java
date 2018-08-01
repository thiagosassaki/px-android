package com.mercadopago.android.px.views;

import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.mvp.MvpView;
import java.util.List;

/**
 * Created by vaserber on 8/17/17.
 */

public interface ReviewPaymentMethodsView extends MvpView {

    void showError(MercadoPagoError error, String requestOrigin);

    void initializeSupportedPaymentMethods(List<PaymentMethod> supportedPaymentMethods);
}
