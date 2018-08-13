package com.mercadopago.android.px.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.mvp.MvpView;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.viewmodel.OneTapModel;

public interface OneTap {

    interface View extends MvpView {

        void cancel();

        void changePaymentMethod();

        void showCardFlow(@NonNull final OneTapModel oneTapModel, @NonNull final Card card);

        void showDetailModal(@NonNull final OneTapModel model);

        void trackConfirm(final OneTapModel model);

        void trackCancel(final String publicKey);

        void trackModal(final OneTapModel model);

        void showPaymentProcessor();

        void showErrorView(@NonNull final MercadoPagoError error);

        void showBusinessResult(final BusinessPayment businessPayment);

        void showPaymentResult(final PaymentResult paymentResult);
    }

    interface Actions {

        void confirmPayment();

        void onTokenResolved();

        void changePaymentMethod();

        void onAmountShowMore();
    }
}
