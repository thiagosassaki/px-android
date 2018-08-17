package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.model.PaymentType;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */

public interface PaymentTypesActivityView {
    void startErrorView(String message, String errorDetail);

    void onValidStart();

    void onInvalidStart(String message);

    void initializePaymentTypes(List<PaymentType> paymentTypes);

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void showLoadingView();

    void stopLoadingView();

    void finishWithResult(PaymentType paymentType);
}
