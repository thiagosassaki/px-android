package com.mercadopago.android.px.internal.features.onetap;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.CardMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.CardPaymentMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodMapper;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;

class OneTapPresenter extends MvpPresenter<OneTap.View, ResourcesProvider> implements OneTap.Actions {

    @NonNull private final OneTapModel model;
    @NonNull private final PluginRepository pluginRepository;

    @NonNull private final CardMapper cardMapper;
    @NonNull private final PaymentMethodMapper paymentMethodMapper;

    /**
     * Creates a OneTap presenter.
     *
     * @param model one tap viewmodel
     * @param pluginRepository
     */
    OneTapPresenter(@NonNull final OneTapModel model,
        @NonNull final PluginRepository pluginRepository) {
        this.model = model;
        this.pluginRepository = pluginRepository;
        cardMapper = new CardMapper();
        paymentMethodMapper = new PaymentMethodMapper();
    }

    @Override
    public void confirmPayment() {
        final OneTapMetadata oneTapMetadata = model.getPaymentMethods().getOneTapMetadata();
        final String paymentTypeId = oneTapMetadata.getPaymentTypeId();
        final String paymentMethodId = oneTapMetadata.getPaymentMethodId();
        getView().trackConfirm(model);
        if (PaymentTypes.isCardPaymentType(paymentTypeId)) {
            getView().showCardFlow(model, cardMapper.map(model));
        } else if (PaymentTypes.isPlugin(paymentTypeId)) {
            getView().showPaymentFlow(pluginRepository.getPluginAsPaymentMethod(paymentMethodId, paymentTypeId));
        } else {
            getView().showPaymentFlow(paymentMethodMapper.map(model.getPaymentMethods()));
        }
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
