package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentHandler;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.viewmodel.mappers.CardMapper;
import com.mercadopago.android.px.viewmodel.mappers.PaymentMethodMapper;

public class PaymentService implements PaymentRepository {

    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final PluginRepository pluginRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final PaymentProcessor paymentProcessor;
    @NonNull private final PaymentMethodMapper paymentMethodMapper;
    @NonNull private final CardMapper cardMapper;
    @NonNull private Context context;

    public PaymentService(@NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final PluginRepository pluginRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final PaymentProcessor paymentProcessor,
        @NonNull final Context context) {

        this.userSelectionRepository = userSelectionRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.pluginRepository = pluginRepository;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.paymentProcessor = paymentProcessor;
        this.context = context;
        paymentMethodMapper = new PaymentMethodMapper();
        cardMapper = new CardMapper();
    }

    @Override
    public void doPayment(@NonNull final OneTapModel oneTapModel, @NonNull final PaymentHandler paymentHandler) {
        final OneTapMetadata oneTapMetadata = oneTapModel.getPaymentMethods().getOneTapMetadata();
        final String paymentTypeId = oneTapMetadata.getPaymentTypeId();
        final String paymentMethodId = oneTapMetadata.getPaymentMethodId();

        if (PaymentTypes.isCardPaymentType(paymentTypeId)) {
            userSelectionRepository.select(cardMapper.map(oneTapModel).getPaymentMethod());
            userSelectionRepository.select(oneTapMetadata.getCard().getAutoSelectedInstallment());
        } else if (PaymentTypes.isPlugin(paymentTypeId)) {
            userSelectionRepository.select(pluginRepository.getPluginAsPaymentMethod(paymentMethodId, paymentTypeId));
        } else {
            userSelectionRepository.select(paymentMethodMapper.map(oneTapModel.getPaymentMethods()));
        }
        doPayment(paymentHandler);
    }

    @Override
    public void doPayment(@NonNull final PaymentHandler paymentHandler) {
        checkPaymentMethod(paymentHandler);
    }

    private void checkPaymentMethod(final PaymentHandler paymentHandler) {
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        if (paymentMethod != null) {
            processPaymentMethod(paymentMethod, paymentHandler);
        } else {
            paymentHandler.onPaymentMethodRequired();
        }
    }

    private void processPaymentMethod(final PaymentMethod paymentMethod, final PaymentHandler paymentHandler) {
        if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
            validateCardInfo(paymentHandler);
        } else {
            createPayment(paymentHandler);
        }
    }

    private void validateCardInfo(final PaymentHandler paymentHandler) {
        //TODO arreglar
        if (userSelectionRepository.getCard() != null && userSelectionRepository.getPayerCost() != null) {
            if (paymentSettingRepository.getToken() != null) {
                createPayment(paymentHandler);
            } else {
                paymentHandler.onCvvRequired(userSelectionRepository.getCard());
            }
        } else {
            paymentHandler.onCardError();
        }
    }

    private void createPayment(final PaymentHandler paymentHandler) {

        final PaymentProcessor.Props processorProperties =
            new PaymentProcessor.Props(createPaymentData(), paymentSettingRepository.getCheckoutPreference());

        if (paymentProcessor.needsVisualPaymentProcessing()) {
            //TODO make
            final Fragment fragment = paymentProcessor.initVisualPayment(processorProperties, paymentHandler);
            paymentHandler.onVisualPayment(fragment);
        } else {
            paymentProcessor.initPayment(context, processorProperties, paymentHandler);
        }
    }

    //TODO remove duplication - Presenter Checkout
    private PaymentData createPaymentData() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(userSelectionRepository.getPaymentMethod());
        paymentData.setPayerCost(userSelectionRepository.getPayerCost());
        paymentData.setIssuer(userSelectionRepository.getIssuer());
        paymentData.setToken(paymentSettingRepository.getToken());
        paymentData.setDiscount(discountRepository.getDiscount());
        paymentData.setTransactionAmount(amountRepository.getAmountToPay());
        //TODO verify identification for payer that comes from boleto selection.
        paymentData.setPayer(paymentSettingRepository.getCheckoutPreference().getPayer());
        return paymentData;
    }
}
