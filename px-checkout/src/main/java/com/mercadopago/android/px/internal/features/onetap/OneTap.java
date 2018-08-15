package com.mercadopago.android.px.internal.features.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.viewmodel.CardPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Token;

public interface OneTap {

    interface View extends MvpView {

        void cancel();

        void changePaymentMethod();

        void showCardFlow(@NonNull final OneTapModel oneTapModel, @NonNull final Card card);

        void showPaymentFlow(@NonNull final PaymentMethod oneTapMetadata);

        void showPaymentFlow(@NonNull final CardPaymentModel cardPaymentModel);

        void showDetailModal(@NonNull final OneTapModel model);

        void trackConfirm(final OneTapModel model);

        void trackCancel(final String publicKey);

        void trackModal(final OneTapModel model);
    }

    interface Actions {

        void confirmPayment();

        void onReceived(@NonNull final Token token);

        void changePaymentMethod();

        void onAmountShowMore();
    }
}
