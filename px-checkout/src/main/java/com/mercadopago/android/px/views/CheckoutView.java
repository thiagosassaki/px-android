package com.mercadopago.android.px.views;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.hooks.Hook;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.mvp.MvpView;
import com.mercadopago.android.px.plugins.model.BusinessPaymentModel;
import com.mercadopago.android.px.viewmodel.OneTapModel;

public interface CheckoutView extends MvpView {

    void showError(MercadoPagoError error);

    void showProgress();

    void showReviewAndConfirm(final boolean isUniquePaymentMethod);

    void showPaymentMethodSelection();

    void showPaymentResult(PaymentResult paymentResult);

    void backToReviewAndConfirm();

    void finishWithPaymentResult();

    void finishWithPaymentResult(Integer customResultCode);

    void finishWithPaymentResult(Payment payment);

    void finishWithPaymentResult(Integer customResultCode, Payment payment);

    void cancelCheckout();

    void cancelCheckout(MercadoPagoError mercadoPagoError);

    void cancelCheckout(Integer customResultCode, Boolean paymentMethodEdited);

    void startPaymentRecoveryFlow(PaymentRecovery paymentRecovery);

    void initializeMPTracker();

    void trackScreen();

    void showHook(final Hook hook, final int requestCode);

    void showPaymentProcessor();

    boolean isActive();

    void fetchImageFromUrl(String url);

    void showBusinessResult(BusinessPaymentModel model);

    void showOneTap(@NonNull final OneTapModel oneTapModel);

    void hideProgress();

    void exitCheckout(int resCode);

    void transitionOut();
}
