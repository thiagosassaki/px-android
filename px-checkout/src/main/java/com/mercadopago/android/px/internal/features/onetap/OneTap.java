package com.mercadopago.android.px.internal.features.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public interface OneTap {

    interface View extends MvpView {

        void cancel();

        void changePaymentMethod();

        void showCardFlow(@NonNull final OneTapModel oneTapModel, @NonNull final Card card);

        void showDetailModal(@NonNull final OneTapModel model);

        void trackConfirm(final OneTapModel model);

        void trackCancel();

        void trackModal(final OneTapModel model);

        void showPaymentProcessor();

        void showErrorView(@NonNull final MercadoPagoError error);

        void showBusinessResult(final BusinessPayment businessPayment);

        void showPaymentResult(final IPayment paymentResult);
    }

    interface Actions {

        void confirmPayment();

        void onTokenResolved();

        void changePaymentMethod();

        void onAmountShowMore();
    }
}
