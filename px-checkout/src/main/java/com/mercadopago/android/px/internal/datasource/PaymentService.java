package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentHandler;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.requests.PaymentBodyIntent;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.services.CheckoutService;
import com.mercadopago.android.px.services.adapters.MPCall;
import com.mercadopago.android.px.services.callbacks.Callback;
import com.mercadopago.android.px.services.core.Settings;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.util.ApiUtil;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.viewmodel.mappers.CardMapper;
import com.mercadopago.android.px.viewmodel.mappers.PaymentDataMapper;
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
    private final CheckoutService checkoutService;

    public PaymentService(@NonNull final CheckoutService checkoutService,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final PluginRepository pluginRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final PaymentProcessor paymentProcessor) {

        this.checkoutService = checkoutService;
//TODO services adapter lo queremos seguir teniendo?
        this.userSelectionRepository = userSelectionRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.pluginRepository = pluginRepository;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.paymentProcessor = paymentProcessor;
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
            //Paying with saved card
            if (paymentSettingRepository.getToken() != null) {
                createPayment(paymentHandler);
            } else {
                paymentHandler.onCvvRequired(userSelectionRepository.getCard());
            }
        } else if (userSelectionRepository.getPaymentMethod() != null) {
            //Paying with new card
            if (userSelectionRepository.getIssuer() == null) {
                paymentHandler.onIssuerRequired();
            } else if (userSelectionRepository.getPayerCost() == null) {
                paymentHandler.onPayerCostRequired();
            } else if (paymentSettingRepository.getToken() == null) {
                paymentHandler.onTokenRequired();
            } else {
                createPayment(paymentHandler);
            }

        } else {
            paymentHandler.onCardError();
        }
    }

    private void createPayment(final PaymentHandler paymentHandler) {

//        final PaymentProcessor.Props processorProperties =
//            new PaymentProcessor.Props(createPaymentData(), paymentSettingRepository.getCheckoutPreference());

        if (paymentProcessor == null) {
            //Payment in Mercado Pago : Por ahora no tenemos procesadora default, por eso es null
            createPaymentInMercadoPago(paymentHandler);
        } else if (paymentProcessor.shouldShowFragmentOnPayment()) {
            //TODO make
//            final Fragment fragment = paymentProcessor.initVisualPayment(processorProperties, paymentHandler);
//            paymentHandler.onVisualPayment(fragment);
        } else {
//            paymentProcessor.initPayment(context, processorProperties, paymentHandler);
        }
    }

    private void createPaymentInMercadoPago(final PaymentHandler paymentHandler) {
        MPCall<Payment> paymentCallback = createPaymentInMercadoPago();
        paymentCallback.enqueue(new Callback<Payment>() {
            @Override
            public void success(final Payment payment) {
                //TODO fix payment data
                final GenericPayment genericPayment = new GenericPayment(payment.getId(), payment.getStatus(),
                    payment.getStatusDetail(), null);
                paymentHandler.onPaymentFinished(genericPayment);
            }

            @Override
            public void failure(final ApiException apiException) {
                paymentHandler.onPaymentError(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_PAYMENT));
            }
        });
    }

    @NonNull
    @Override
    public MPCall<Payment> createPaymentInMercadoPago() {

        return new MPCall<Payment>() {
            @Override
            public void enqueue(final Callback<Payment> callback) {
                newRequest().enqueue(getInternalCallback(callback));
            }

            @Override
            public void execute(final Callback<Payment> callback) {
                newRequest().execute(getInternalCallback(callback));
            }

            @NonNull /* default */ Callback<Payment> getInternalCallback(
                final Callback<Payment> callback) {
                return new Callback<Payment>() {
                    @Override
                    public void success(final Payment payment) {
                        callback.success(payment);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.failure(apiException);
                    }
                };
            }
        };
    }

    @NonNull /* default */ MPCall<Payment> newRequest() {

        final PaymentDataMapper paymentDataMapper = new PaymentDataMapper(paymentSettingRepository,
            userSelectionRepository, discountRepository);
        final PaymentBodyIntent paymentBodyIntent = paymentDataMapper.map(createPaymentData());

        //TODO el transaction ID lo cambié a que pase solo en el HEADER (se estaba pasando tambien en el body)
        //TODO ojo que cada vez que se pide un transactionID devuelve uno nuevo
        return checkoutService.createPayment(Settings.servicesVersion, paymentSettingRepository.getTransactionId(),
            paymentBodyIntent);

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
