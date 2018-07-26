package com.mercadopago.android.px.onetap;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.repository.PaymentHandler;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.mvp.MvpPresenter;
import com.mercadopago.android.px.mvp.ResourcesProvider;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.plugins.model.Processor;
import com.mercadopago.android.px.viewmodel.OneTapModel;

class OneTapPresenter extends MvpPresenter<OneTap.View, ResourcesProvider> implements OneTap.Actions, Processor {

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
            public void onPaymentFinished(final PluginPayment payment) {
                payment.process(OneTapPresenter.this);
            }

            @Override
            public void onPaymentError(final MercadoPagoError error) {
                //TODO same thing -> activity
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

            @Override
            public void onVisualPayment(final Fragment fragment) {
                //TODO - Caso instores vending machine.
            }


        });
    }

    @Override
    public void onReceived(@NonNull final Token token) {
        //TODO REMOVE
        confirmPayment();
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

    @Override
    public void process(final BusinessPayment businessPayment) {

    }

    @Override
    public void process(final GenericPayment genericPayment) {
        final PaymentResult paymentResult = toPaymentResult(genericPayment);
        CheckoutStore.getInstance().setPaymentResult(paymentResult);
        //TODO hacer algo en la vista al terminar el pago
        //getView().onPaymentProcessed(paymentResult);
        //setResult(RESULT_OK);
        //finish();
        //TODO same thing Activity.
    }

    private PaymentResult toPaymentResult(@NonNull final GenericPayment genericPayment) {

        final Payment payment = new Payment();
        payment.setId(genericPayment.paymentId);
        payment.setPaymentMethodId(genericPayment.paymentData.getPaymentMethod().getId());
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(genericPayment.status);
        payment.setStatusDetail(genericPayment.statusDetail);

        return new PaymentResult.Builder()
            .setPaymentData(genericPayment.paymentData)
            .setPaymentId(payment.getId())
            .setPaymentStatus(payment.getStatus())
            .setPaymentStatusDetail(payment.getStatusDetail())
            .build();
    }

}
