package com.mercadopago.android.px.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentHandler;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.mvp.MvpPresenter;
import com.mercadopago.android.px.mvp.ResourcesProvider;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.viewmodel.mappers.CardPaymentMapper;

class OneTapPresenter extends MvpPresenter<OneTap.View, ResourcesProvider> implements OneTap.Actions {

    @NonNull private final OneTapModel model;
    @NonNull private final PaymentRepository paymentRepository;

    OneTapPresenter(@NonNull final OneTapModel model,
        @NonNull final PaymentRepository paymentRepository) {
        this.model = model;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void confirmPayment() {
        getView().trackConfirm(model);

        paymentRepository.doPayment(model, new PaymentHandler() {
            @Override
            public void onPaymentError(@NonNull final ApiException error) {

            }

            @Override
            public void onPaymentSuccess(@NonNull final Payment payment) {

            }

            @Override
            public void onPaymentMethodRequired() {
                //TODO no debería pasar.
            }

            @Override
            public void onCvvRequired(@NonNull final Card card) {
                getView().showCardFlow(model, card);
            }

            @Override
            public void onCardError() {
                getView().showCardFlow(model, null);
            }
        });
    }

    @Override
    public void onReceived(@NonNull final Token token) {
        getView().showPaymentFlow(new CardPaymentMapper(token).map(model));
    }

    @Override
    public void changePaymentMethod() {
        getView().changePaymentMethod();
    }

    @Override
    public void onAmountShowMore() {
        getView().trackModal(model);
        getView().showDetailModal(model);
    }

    public void cancel() {
        getView().cancel();
        getView().trackCancel(model.getPublicKey());
    }
}
