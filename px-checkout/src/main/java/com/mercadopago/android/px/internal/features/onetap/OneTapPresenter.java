package com.mercadopago.android.px.internal.features.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.features.explode.ExplodeDecoratorMapper;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandler;
import com.mercadopago.android.px.internal.features.explode.ExplodingFragment;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

/* default */ class OneTapPresenter extends MvpPresenter<OneTap.View, ResourcesProvider>
    implements OneTap.Actions, PaymentServiceHandler {

    private static final String TAG = OneTapPresenter.class.getName();
    @NonNull private final OneTapModel model;
    @NonNull private final PaymentRepository paymentRepository;
    private final ExplodeDecoratorMapper explodeDecoratorMapper;

    //TODO refactor
    private int yButtonPosition;
    private int buttonHeight;

    /* default */ OneTapPresenter(@NonNull final OneTapModel model,
        @NonNull final PaymentRepository paymentRepository) {
        this.model = model;
        this.paymentRepository = paymentRepository;
        explodeDecoratorMapper = new ExplodeDecoratorMapper();
    }

    @Override
    public void confirmPayment(final int yButtonPosition, final int buttonHeight) {
        getView().trackConfirm(model);
        //TODO persist this data.
        this.yButtonPosition = yButtonPosition;
        this.buttonHeight = buttonHeight;

        getView().hideToolbar();
        getView().hideConfirmButton();
        getView().startLoadingButton(yButtonPosition, buttonHeight, paymentRepository.getPaymentTimeout());
        paymentRepository.startOneTapPayment(model, this);
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
        if (isViewAttached()) {
            getView().cancel();
            getView().trackCancel();
        }
    }

    @Override
    public void onTokenResolved() {
        if (isViewAttached()) {
            //TODO fix yButtonPosition and buttonHeight persistance
            confirmPayment(yButtonPosition, buttonHeight);
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final Payment payment) {
        if (isViewAttached()) {
            getView().showLoadingFor(explodeDecoratorMapper.map(payment),
                new ExplodingFragment.ExplodingAnimationListener() {
                    @Override
                    public void onAnimationFinished() {
                        getView().showPaymentResult(payment);
                    }
                });
        }
    }

    /**
     * When there is no visual interaction needed this callback is called.
     *
     * @param genericPayment plugin payment.
     */
    @Override
    public void onPaymentFinished(@NonNull final GenericPayment genericPayment) {
        if (isViewAttached()) {
            getView().showLoadingFor(explodeDecoratorMapper.map(genericPayment),
                new ExplodingFragment.ExplodingAnimationListener() {
                    @Override
                    public void onAnimationFinished() {
                        getView().showPaymentResult(genericPayment);
                    }
                });
        }
    }

    /**
     * When there is no visual interaction needed this callback is called.
     *
     * @param businessPayment plugin payment.
     */
    @Override
    public void onPaymentFinished(@NonNull final BusinessPayment businessPayment) {
        if (isViewAttached()) {
            getView().showLoadingFor(explodeDecoratorMapper.map(businessPayment),
                new ExplodingFragment.ExplodingAnimationListener() {
                    @Override
                    public void onAnimationFinished() {
                        getView().showPaymentResult(businessPayment);
                    }
                });
        }
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        //This method calls to Checkout activity to manage esc, it's important to check
        // all this behaviour ahead.
        if (isViewAttached()) {
            getView().cancelLoading();
            getView().showErrorView(error);
        }
    }

    @Override
    public void onVisualPayment() {
        if (isViewAttached()) {
            getView().showPaymentProcessor();
        }
    }

    @Override
    public void onCvvRequired(@NonNull final Card card) {
        if (isViewAttached()) {
            getView().cancelLoading();
            getView().showCardFlow(model, card);
        }
    }

    @Override
    public void onRecoverPaymentEscInvalid() {
        if(isViewAttached()){
            getView().onRecoverPaymentEscInvalid();
        }
    }

    @Override
    public void onViewResumed(final OneTapModel model) {
        getView().updateViews(model);
    }

}