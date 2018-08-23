package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentServiceHandler;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.CardMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodMapper;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public class PaymentService implements PaymentRepository {

    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final PluginRepository pluginRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final PaymentProcessor paymentProcessor;
    @NonNull private final Context context;
    @NonNull private final PaymentMethodMapper paymentMethodMapper;
    @NonNull private final CardMapper cardMapper;

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

    /**
     * This method presets all user information ahead before the payment is processed.
     *
     * @param oneTapModel onetap information
     * @param paymentServiceHandler callback handler
     */
    @Override
    public void startOneTapPayment(@NonNull final OneTapModel oneTapModel,
        @NonNull final PaymentServiceHandler paymentServiceHandler) {
        final OneTapMetadata oneTapMetadata = oneTapModel.getPaymentMethods().getOneTapMetadata();
        final String paymentTypeId = oneTapMetadata.getPaymentTypeId();
        final String paymentMethodId = oneTapMetadata.getPaymentMethodId();

        if (PaymentTypes.isCardPaymentType(paymentTypeId)) {
            // Saved card.
            final Card card = cardMapper.map(oneTapModel);
            userSelectionRepository.select(card);
            userSelectionRepository.select(oneTapMetadata.getCard().getAutoSelectedInstallment());
        } else if (PaymentTypes.isPlugin(paymentTypeId)) {
            // Account money plugin / No second factor.
            userSelectionRepository.select(pluginRepository.getPluginAsPaymentMethod(paymentMethodId, paymentTypeId));
        } else {
            // Other - not implemented
            userSelectionRepository.select(paymentMethodMapper.map(oneTapModel.getPaymentMethods()));
        }

        startPayment(paymentServiceHandler);
    }

    @Override
    public void startPayment(@NonNull final PaymentServiceHandler paymentServiceHandler) {
        checkPaymentMethod(paymentServiceHandler);
    }

    private void checkPaymentMethod(final PaymentServiceHandler paymentServiceHandler) {
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        if (paymentMethod != null) {
            processPaymentMethod(paymentMethod, paymentServiceHandler);
        } else {
            paymentServiceHandler.onPaymentMethodRequired();
        }
    }

    private void processPaymentMethod(final PaymentMethod paymentMethod,
        final PaymentServiceHandler paymentServiceHandler) {
        if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
            validateCardInfo(paymentServiceHandler);
        } else {
            createPayment(paymentServiceHandler);
        }
    }

    private void validateCardInfo(final PaymentServiceHandler paymentServiceHandler) {
        //TODO improve
        if (userSelectionRepository.hasCardSelected() && userSelectionRepository.getPayerCost() != null) {
            //Paying with saved card
            if (paymentSettingRepository.getToken() != null) {
                createPayment(paymentServiceHandler);
            } else {
                paymentServiceHandler.onCvvRequired(userSelectionRepository.getCard());
            }
        } else if (userSelectionRepository.getPaymentMethod() != null) {
            //Paying with new card
            if (userSelectionRepository.getIssuer() == null) {
                paymentServiceHandler.onIssuerRequired();
            } else if (userSelectionRepository.getPayerCost() == null) {
                paymentServiceHandler.onPayerCostRequired();
            } else if (paymentSettingRepository.getToken() == null) {
                paymentServiceHandler.onTokenRequired();
            } else {
                createPayment(paymentServiceHandler);
            }

        } else {
            paymentServiceHandler.onCardError();
        }
    }

    private void createPayment(final PaymentServiceHandler paymentServiceHandler) {

        if (paymentProcessor.shouldShowFragmentOnPayment()) {
            paymentServiceHandler.onVisualPayment();
        } else {
            final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
            final PaymentProcessor.CheckoutData checkoutData =
                new PaymentProcessor.CheckoutData(getPaymentData(), checkoutPreference);

            paymentProcessor.startPayment(checkoutData, context, paymentServiceHandler);
        }
    }

    @NonNull
    @Override
    public PaymentData getPaymentData() {
        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(userSelectionRepository.getPaymentMethod());
        paymentData.setPayerCost(userSelectionRepository.getPayerCost());
        paymentData.setIssuer(userSelectionRepository.getIssuer());
        paymentData.setToken(paymentSettingRepository.getToken());
        paymentData.setDiscount(discountRepository.getDiscount());
        paymentData.setTransactionAmount(amountRepository.getAmountToPay());
        //se agrego payer info a la pref - BOLBRADESCO
        paymentData.setPayer(paymentSettingRepository.getCheckoutPreference().getPayer());
        return paymentData;
    }

    @NonNull
    @Override
    public PaymentResult createPaymentResult(@NonNull final IPayment payment) {
        return new PaymentResult.Builder()
            .setPaymentData(getPaymentData())
            .setPaymentId(payment.getId())
            .setPaymentStatus(payment.getPaymentStatus())
            .setStatementDescription(payment.getStatementDescription())
            .setPaymentStatusDetail(payment.getPaymentStatusDetail())
            .build();
    }
}
