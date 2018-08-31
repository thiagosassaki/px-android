package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CardTokenException;

/**
 * Created by vaserber on 10/26/16.
 */

public interface SecurityCodeActivityView extends MvpView {
    void setSecurityCodeInputMaxLength(int length);

    void showError(MercadoPagoError error, String requestOrigin);

    void setErrorView(CardTokenException exception);

    void clearErrorView();

    void showLoadingView();

    void stopLoadingView();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void finishWithResult();

    void initialize();

    void showTimer();

    void trackScreen();

    void showBackSecurityCodeCardView();

    void showFrontSecurityCodeCardView();
}
