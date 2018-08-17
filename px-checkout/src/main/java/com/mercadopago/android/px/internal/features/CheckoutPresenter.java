package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.datasource.CheckoutStore;
import com.mercadopago.android.px.internal.datasource.PluginInitializationTask;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import com.mercadopago.android.px.internal.features.hooks.HookHelper;
import com.mercadopago.android.px.internal.features.providers.CheckoutProvider;
import com.mercadopago.android.px.internal.navigation.DefaultPaymentMethodDriver;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.viewmodel.mappers.BusinessModelMapper;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CheckoutPresenter extends MvpPresenter<CheckoutView, CheckoutProvider> {

    private static final String INTERNAL_SERVER_ERROR_FIRST_DIGIT = "5";

    @NonNull private final CheckoutStateModel state;

    @NonNull private final PluginRepository pluginRepository;

    @NonNull
    private final GroupsRepository groupsRepository;
    @NonNull
    private final DiscountRepository discountRepository;
    @NonNull
    private final PaymentSettingRepository paymentSettingRepository;
    @NonNull
    private final AmountRepository amountRepository;
    @NonNull
    private final UserSelectionRepository userSelectionRepository;

    @NonNull
    private final AdvancedConfiguration advancedConfiguration;

    private transient FailureRecovery failureRecovery;

    private PluginInitializationTask pluginInitializationTask; //instance saved as attribute to cancel and avoid crash

    public CheckoutPresenter(@NonNull final CheckoutStateModel persistentData,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final GroupsRepository groupsRepository,
        @NonNull final PluginRepository pluginRepository) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.amountRepository = amountRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
        this.groupsRepository = groupsRepository;
        advancedConfiguration = paymentSettingRepository.getAdvancedConfiguration();
        state = persistentData;
        this.pluginRepository = pluginRepository;
    }

    public Serializable getState() {
        return state;
    }

    public void initialize() {
        getView().showProgress();
        configurePreference();
    }

    private void configurePreference() {
        if (paymentSettingRepository.getCheckoutPreference() != null) {
            startCheckoutForPreference();
        } else {
            retrieveCheckoutPreference(paymentSettingRepository.getCheckoutPreferenceId());
        }
    }

    private void startCheckoutForPreference() {
        try {
            getCheckoutPreference().validate();
            getView().initializeMPTracker();
            getView().trackScreen();
            startCheckout();
        } catch (CheckoutPreferenceException e) {
            final String message = getResourcesProvider().getCheckoutExceptionMessage(e);
            getView().showError(new MercadoPagoError(message, false));
        }
    }

    private void startCheckout() {
        getResourcesProvider().fetchFonts();
        fetchImages();
        initializePluginsData();
    }

    private void fetchImages() {
        //TODO move this mechanism
        final PaymentResultScreenConfiguration resultPreference =
            advancedConfiguration.getPaymentResultScreenConfiguration();
        if (isViewAttached()) {
            if (!TextUtil.isEmpty(resultPreference.getApprovedUrlIcon())) {
                getView().fetchImageFromUrl(resultPreference.getApprovedUrlIcon());
            }
            if (!TextUtil.isEmpty(resultPreference.getRejectedUrlIcon())) {
                getView().fetchImageFromUrl(resultPreference.getRejectedUrlIcon());
            }
            if (!TextUtil.isEmpty(resultPreference.getPendingUrlIcon())) {
                getView().fetchImageFromUrl(resultPreference.getPendingUrlIcon());
            }
        }
    }

    private void initializePluginsData() {
        pluginInitializationTask = pluginRepository.getInitTask();
        pluginInitializationTask.execute(getDataInitializationCallback());
    }

    @NonNull
    private PluginInitializationTask.DataInitializationCallbacks getDataInitializationCallback() {
        return new PluginInitializationTask.DataInitializationCallbacks() {
            @Override
            public void onDataInitialized() {
                finishInitializingPluginsData();
            }

            @Override
            public void onFailure(@NonNull final Exception e) {
                finishInitializingPluginsData();
            }
        };
    }

    private void finishInitializingPluginsData() {
        discountRepository.configureDiscountAutomatically(amountRepository.getAmountToPay())
            .enqueue(new Callback<Boolean>() {
                @Override
                public void success(final Boolean automatic) {
                    retrievePaymentMethodSearch();
                }

                @Override
                public void failure(final ApiException apiException) {
                    retrievePaymentMethodSearch();
                }
            });
    }

    private void retrievePaymentMethodSearch() {
        if (isViewAttached()) {
            groupsRepository.getGroups().enqueue(new Callback<PaymentMethodSearch>() {
                @Override
                public void success(final PaymentMethodSearch paymentMethodSearch) {
                    if (isViewAttached()) {
                        startFlow(paymentMethodSearch);
                    }
                }

                @Override
                public void failure(final ApiException apiException) {
                    if (isViewAttached()) {
                        getView()
                            .showError(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PAYMENT_METHODS));
                    }
                }
            });
        }
    }

    /* default */ void startFlow(final PaymentMethodSearch paymentMethodSearch) {

        new DefaultPaymentMethodDriver(paymentMethodSearch,
            paymentSettingRepository.getCheckoutPreference().getPaymentPreference())
            .drive(new DefaultPaymentMethodDriver.PaymentMethodDriverCallback() {
                @Override
                public void driveToCardVault(@NonNull final Card card) {
                    userSelectionRepository.select(card.getPaymentMethod());
                    getView().showSavedCardFlow(card);
                }

                @Override
                public void driveToNewCardFlow() {
                    getView().showNewCardFlow();
                }

                @Override
                public void doNothing() {
                    noDefaultPaymentMethods(paymentMethodSearch);
                }
            });
    }

    /* default */ void noDefaultPaymentMethods(final PaymentMethodSearch paymentMethodSearch) {
        saveIsOneTap(paymentMethodSearch);
        savePaymentMethodQuantity(paymentMethodSearch);

        if (state.isOneTap) {
            getView().hideProgress();
            getView().showOneTap(OneTapModel.from(paymentMethodSearch, paymentSettingRepository));
        } else {
            getView().showPaymentMethodSelection();
        }
    }

    private void showReviewAndConfirm() {
        state.editPaymentMethodFromReviewAndConfirm = false;
        getView().showReviewAndConfirm(isUniquePaymentMethod());
    }

    public boolean isESCEnabled() {
        return paymentSettingRepository.getAdvancedConfiguration().isEscEnabled();
    }

    public Card getSelectedCard() {
        return state.selectedCard;
    }

    private void retrieveCheckoutPreference(final String checkoutPreferenceId) {
        getResourcesProvider().getCheckoutPreference(checkoutPreferenceId,
            new TaggedCallback<CheckoutPreference>(ApiUtil.RequestOrigin.GET_PREFERENCE) {

                @Override
                public void onSuccess(final CheckoutPreference checkoutPreference) {
                    paymentSettingRepository.configure(checkoutPreference);
                    if (isViewAttached()) {
                        startCheckoutForPreference();
                    }
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    if (isViewAttached()) {
                        getView().showError(error);
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                retrieveCheckoutPreference(checkoutPreferenceId);
                            }
                        });
                    }
                }
            });
    }

    public void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError) {
        if (isIdentificationInvalidInPayment(mercadoPagoError)) {
            getView().showPaymentMethodSelection();
        } else {
            cancelCheckout();
        }
    }

    private boolean isIdentificationInvalidInPayment(final MercadoPagoError mercadoPagoError) {
        boolean identificationInvalid = false;
        if (mercadoPagoError != null && mercadoPagoError.isApiException()) {
            List<Cause> causeList = mercadoPagoError.getApiException().getCause();
            if (causeList != null && !causeList.isEmpty()) {
                Cause cause = causeList.get(0);
                if (cause.getCode().equals(ApiException.ErrorCodes.INVALID_IDENTIFICATION_NUMBER)) {
                    identificationInvalid = true;
                }
            }
        }
        return identificationInvalid;
    }

    public void onPaymentMethodSelectionResponse(
        final Token token,
        final Card card,
        final Payer payer) {
        state.createdToken = token;
        state.selectedCard = card;
        state.collectedPayer = payer;

        onPaymentMethodSelected();
    }

    private void onPaymentMethodSelected() {
        if (!showHook2(createPaymentData())) {
            hook2Continue();
        }
    }

    public void createPayment() {
        //TODO
        //TODO add payment handling
        getView().showProgress();
        final PaymentData paymentData = createPaymentData();
        CheckoutStore.getInstance().setPaymentData(paymentData);
        getView().showPaymentProcessor();

            /* getResourcesProvider().createPayment(paymentSettingRepository.getTransactionId(),
                getCheckoutPreference(),
                paymentData,
                paymentConfiguration.getCheckoutPreference().isBinaryMode(),
                null, //TODO ver.
                new TaggedCallback<Payment>(ApiUtil.RequestOrigin.CREATE_PAYMENT) {
                    @Override
                    public void onSuccess(final Payment payment) {
                        if (isViewAttached()) {
                            getView().hideProgress();
                            state.createdPayment = payment;
                            PaymentResult paymentResult = createPaymentResult(payment, paymentData);
                            checkStartPaymentResultActivity(paymentResult);
                        }
                    }

                    @Override
                    public void onFailure(final MercadoPagoError error) {
                        if (isViewAttached()) {
                            getView().hideProgress();
                            resolvePaymentError(error, paymentData);
                        }
                    }
                });

                */
    }

    @VisibleForTesting
    void resolvePaymentError(final MercadoPagoError error, final PaymentData paymentData) {
        final boolean invalidEsc = getResourcesProvider().manageEscForError(error, paymentData);
        if (invalidEsc) {
            continuePaymentWithoutESC();
        } else {
            recoverCreatePayment(error);
        }
    }

    private void continuePaymentWithoutESC() {
        state.paymentRecovery =
            new PaymentRecovery(paymentSettingRepository.getToken(), userSelectionRepository.getPaymentMethod(),
                userSelectionRepository.getPayerCost(), userSelectionRepository.getIssuer(),
                Payment.StatusCodes.STATUS_REJECTED,
                Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        getView().startPaymentRecoveryFlow(state.paymentRecovery);
    }

    private void recoverCreatePayment(final MercadoPagoError error) {
        setFailureRecovery(new FailureRecovery() {
            @Override
            public void recover() {
                createPayment();
            }
        });
        resolvePaymentFailure(error);
    }

    private void resolvePaymentFailure(final MercadoPagoError mercadoPagoError) {

        if (isPaymentProcessing(mercadoPagoError)) {
            resolveProcessingPaymentStatus();
        } else if (isInternalServerError(mercadoPagoError)) {
            resolveInternalServerError(mercadoPagoError);
        } else if (isBadRequestError(mercadoPagoError)) {
            resolveBadRequestError(mercadoPagoError);
        } else {
            getView().showError(mercadoPagoError);
        }
    }

    private boolean isBadRequestError(final MercadoPagoError mercadoPagoError) {
        return mercadoPagoError != null && mercadoPagoError.getApiException() != null
            && (mercadoPagoError.getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST);
    }

    private boolean isInternalServerError(final MercadoPagoError mercadoPagoError) {
        return mercadoPagoError != null && mercadoPagoError.getApiException() != null
            && String.valueOf(mercadoPagoError.getApiException().getStatus())
            .startsWith(INTERNAL_SERVER_ERROR_FIRST_DIGIT);
    }

    private boolean isPaymentProcessing(final MercadoPagoError mercadoPagoError) {
        return mercadoPagoError != null && mercadoPagoError.getApiException() != null
            && mercadoPagoError.getApiException().getStatus() == ApiUtil.StatusCodes.PROCESSING;
    }

    private void resolveInternalServerError(final MercadoPagoError mercadoPagoError) {
        getView().showError(mercadoPagoError);
        setFailureRecovery(new FailureRecovery() {
            @Override
            public void recover() {
                createPayment();
            }
        });
    }

    private void resolveProcessingPaymentStatus() {
        state.createdPayment = new Payment();
        state.createdPayment.setStatus(Payment.StatusCodes.STATUS_IN_PROCESS);
        state.createdPayment.setStatusDetail(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY);
        final PaymentResult paymentResult = createPaymentResult(state.createdPayment, createPaymentData());
        getView().showPaymentResult(paymentResult);
    }

    private void resolveBadRequestError(final MercadoPagoError mercadoPagoError) {
        getView().showError(mercadoPagoError);
    }

    public void onPaymentMethodSelectionError(final MercadoPagoError mercadoPagoError) {
        if (!state.editPaymentMethodFromReviewAndConfirm) {
            getView().cancelCheckout(mercadoPagoError);
        } else {
            state.editPaymentMethodFromReviewAndConfirm = false;
            getView().backToReviewAndConfirm();
        }
    }

    public void onPaymentMethodSelectionCancel() {
        if (!state.editPaymentMethodFromReviewAndConfirm) {
            cancelCheckout();
        } else {
            state.editPaymentMethodFromReviewAndConfirm = false;
            getView().backToReviewAndConfirm();
        }
    }

    public void onPaymentConfirmation() {
        if (!showHook3(createPaymentData())) {
            createPayment();
        }
    }

    public void onReviewAndConfirmCancel() {
        if (isUniquePaymentMethod()) {
            getView().cancelCheckout();
        } else {
            state.paymentMethodEdited = true;
            getView().showPaymentMethodSelection();
            //Back button in R&C
            getView().transitionOut();
        }
    }

    public void onReviewAndConfirmError(final MercadoPagoError mercadoPagoError) {
        getView().cancelCheckout(mercadoPagoError);
    }

    public void onPaymentResultCancel(final String nextAction) {
        if (!TextUtil.isEmpty(nextAction)) {
            if (nextAction.equals(PaymentResult.SELECT_OTHER_PAYMENT_METHOD)) {
                state.paymentMethodEdited = true;
                getView().showPaymentMethodSelection();
            } else if (nextAction.equals(PaymentResult.RECOVER_PAYMENT)) {
                recoverPayment();
            }
        }
    }

    public void onPaymentResultResponse() {
        finishCheckout();
    }

    public void onCardFlowResponse() {
        if (isRecoverableTokenProcess()) {
            createPayment();
        } else {
            onPaymentMethodSelected();
        }
    }

    public void onCardFlowError(final MercadoPagoError mercadoPagoError) {
        getView().cancelCheckout(mercadoPagoError);
    }

    public void onCardFlowCancel() {
        groupsRepository.getGroups().execute(new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                new DefaultPaymentMethodDriver(paymentMethodSearch,
                    paymentSettingRepository.getCheckoutPreference().getPaymentPreference()).drive(
                    new DefaultPaymentMethodDriver.PaymentMethodDriverCallback() {
                        @Override
                        public void driveToCardVault(@NonNull final Card card) {
                            cancelCheckout();
                        }

                        @Override
                        public void driveToNewCardFlow() {
                            cancelCheckout();
                        }

                        @Override
                        public void doNothing() {
                            state.paymentMethodEdited = true;
                            getView().showPaymentMethodSelection();
                        }
                    });
            }

            @Override
            public void failure(final ApiException apiException) {
                state.paymentMethodEdited = true;
                getView().showPaymentMethodSelection();
            }
        });
    }

    public void onCustomReviewAndConfirmResponse(final Integer customResultCode) {
        getView().cancelCheckout(customResultCode, state.paymentMethodEdited);
    }

    public void onCustomPaymentResultResponse(final Integer customResultCode) {
        if (state.createdPayment == null) {
            getView().finishWithPaymentResult(customResultCode);
        } else {
            getView().finishWithPaymentResult(customResultCode, state.createdPayment);
        }
    }

    private void savePaymentMethodQuantity(final PaymentMethodSearch paymentMethodSearch) {
        final int pluginCount = pluginRepository.getPaymentMethodPluginCount();
        int groupCount = 0;
        int customCount = 0;

        if (paymentMethodSearch != null && paymentMethodSearch.hasSearchItems()) {
            groupCount = paymentMethodSearch.getGroups().size();
            if (pluginCount == 0 && groupCount == 1 && paymentMethodSearch.getGroups().get(0).isGroup()) {
                state.isUniquePaymentMethod = false;
            }
        }

        if (paymentMethodSearch != null && paymentMethodSearch.hasCustomSearchItems()) {
            customCount = paymentMethodSearch.getCustomSearchItems().size();
        }

        state.isUniquePaymentMethod = groupCount + customCount + pluginCount == 1;
    }

    private PaymentResult createPaymentResult(final Payment payment, final PaymentData paymentData) {
        return new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(payment.getId())
            .setPaymentStatus(payment.getStatus())
            .setPaymentStatusDetail(payment.getStatusDetail())
            .setPayerEmail(getCheckoutPreference().getPayer().getEmail())
            .setStatementDescription(payment.getStatementDescriptor())
            .build();
    }

    private PaymentData createPaymentData() {

        final PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(userSelectionRepository.getPaymentMethod());
        paymentData.setPayerCost(userSelectionRepository.getPayerCost());
        paymentData.setIssuer(userSelectionRepository.getIssuer());
        paymentData.setDiscount(discountRepository.getDiscount());
        paymentData.setToken(paymentSettingRepository.getToken());
        paymentData.setCouponCode(discountRepository.getDiscountCode());
        paymentData.setTransactionAmount(amountRepository.getAmountToPay());
        final Payer payer = createPayerFrom(getCheckoutPreference().getPayer(), state.collectedPayer);
        paymentData.setPayer(payer);

        return paymentData;
    }

    private Payer createPayerFrom(final Payer checkoutPreferencePayer,
        final Payer collectedPayer) {
        Payer payerForPayment;
        if (checkoutPreferencePayer != null && collectedPayer != null) {
            payerForPayment = copy(checkoutPreferencePayer);
            payerForPayment.setFirstName(collectedPayer.getFirstName());
            payerForPayment.setLastName(collectedPayer.getLastName());
            payerForPayment.setIdentification(collectedPayer.getIdentification());
        } else {
            payerForPayment = checkoutPreferencePayer;
        }
        return payerForPayment;
    }

    private Payer copy(final Payer original) {
        return JsonUtil.getInstance().fromJson(JsonUtil.getInstance().toJson(original), Payer.class);
    }

    private void recoverPayment() {
        try {
            final PaymentResult paymentResult =
                CheckoutStore.getInstance().getPaymentResult();
            final String paymentStatus =
                state.createdPayment == null ? paymentResult.getPaymentStatus() : state.createdPayment.getStatus();
            final String paymentStatusDetail = state.createdPayment == null ? paymentResult.getPaymentStatusDetail()
                : state.createdPayment.getStatusDetail();
            state.paymentRecovery =
                new PaymentRecovery(paymentSettingRepository.getToken(), userSelectionRepository.getPaymentMethod(),
                    userSelectionRepository.getPayerCost(),
                    userSelectionRepository.getIssuer(), paymentStatus, paymentStatusDetail);
            getView().startPaymentRecoveryFlow(state.paymentRecovery);
        } catch (IllegalStateException e) {
            String message = getResourcesProvider().getCheckoutExceptionMessage(e);
            getView().showError(new MercadoPagoError(message, e.getMessage(), false));
        }
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        } else {
            IllegalStateException e = new IllegalStateException("Failure recovery not defined");
            getView().showError(new MercadoPagoError(getResourcesProvider().getCheckoutExceptionMessage(e), false));
        }
    }

    public void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    private boolean isRecoverableTokenProcess() {
        return state.paymentRecovery != null && state.paymentRecovery.isTokenRecoverable();
    }

    public PaymentMethod getSelectedPaymentMethod() {
        return userSelectionRepository.getPaymentMethod();
    }

    public Issuer getIssuer() {
        return userSelectionRepository.getIssuer();
    }

    public Token getCreatedToken() {
        return paymentSettingRepository.getToken();
    }

    public Payment getCreatedPayment() {
        return state.createdPayment;
    }

    public CheckoutPreference getCheckoutPreference() {
        return paymentSettingRepository.getCheckoutPreference();
    }

    public Discount getDiscount() {
        return discountRepository.getDiscount();
    }

    public Campaign getCampaign() {
        return discountRepository.getCampaign();
    }

    public Boolean getShowBankDeals() {
        return paymentSettingRepository.getAdvancedConfiguration().isBankDealsEnabled();
    }

    //### Hooks #####################

    private boolean showHook2(final PaymentData paymentData) {
        return showHook2(paymentData, MercadoPagoComponents.Activities.HOOK_2);
    }

    private boolean showHook2(final PaymentData paymentData, final int requestCode) {
        final Map<String, Object> data = CheckoutStore.getInstance().getData();
        final Hook hook = HookHelper.activateAfterPaymentMethodConfig(
            CheckoutStore.getInstance().getCheckoutHooks(), paymentData, data);
        if (hook != null && getView() != null) {
            getView().showHook(hook, requestCode);
            return true;
        }
        return false;
    }

    private boolean showHook3(final PaymentData paymentData) {
        return showHook3(paymentData, MercadoPagoComponents.Activities.HOOK_3);
    }

    private boolean showHook3(final PaymentData paymentData, final int requestCode) {
        final Map<String, Object> data = CheckoutStore.getInstance().getData();
        final Hook hook = HookHelper.activateBeforePayment(
            CheckoutStore.getInstance().getCheckoutHooks(), paymentData, data);
        if (hook != null && getView() != null) {
            getView().showHook(hook, requestCode);
            return true;
        }
        return false;
    }

    public void hook2Continue() {
        state.editPaymentMethodFromReviewAndConfirm = false;
        showReviewAndConfirm();
    }

    public void cancelInitialization() {
        if (pluginInitializationTask != null) {
            pluginInitializationTask.cancel();
        }
    }

    public void checkStartPaymentResultActivity(final PaymentResult paymentResult) {
        final PaymentData paymentData = paymentResult.getPaymentData();
        final String paymentStatus = paymentResult.getPaymentStatus();
        final String paymentStatusDetail = paymentResult.getPaymentStatusDetail();
        if (getResourcesProvider().manageEscForPayment(paymentData, paymentStatus, paymentStatusDetail)) {
            continuePaymentWithoutESC();
        } else {
            getView().showPaymentResult(paymentResult);
        }
    }

    public void onBusinessResult(final BusinessPayment businessPayment) {
        //TODO look for a better option than singleton, it make it not testeable.
        final PaymentData paymentData = CheckoutStore.getInstance().getPaymentData();
        getResourcesProvider().manageEscForPayment(paymentData,
            businessPayment.getPaymentStatus(),
            businessPayment.getPaymentStatusDetail());

        getView().showBusinessResult(
            new BusinessModelMapper(discountRepository, paymentSettingRepository, amountRepository)
                .map(businessPayment));
    }

    private void finishCheckout() {
        if (state.createdPayment == null) {
            getView().finishWithPaymentResult();
        } else {
            getView().finishWithPaymentResult(state.createdPayment);
        }
    }

    /**
     * Send intention to close checkout
     * if the checkout has oneTap data then it should not close.
     */
    public void cancelCheckout() {//TODO FIX
        if (state.isOneTap) {
            getView().hideProgress();
        } else {
            getView().cancelCheckout();
        }
    }

    private void saveIsOneTap(final PaymentMethodSearch paymentMethodSearch) {
        state.isOneTap = paymentMethodSearch.hasOneTapMetadata();
    }

    /**
     * Close checkout with resCode
     */
    public void exitWithCode(final int resCode) {
        getView().exitCheckout(resCode);
    }

    public void onChangePaymentMethodFromReviewAndConfirm() {
        //TODO remove when navigation is corrected and works with stack.
        onChangePaymentMethod(true);
    }

    public void onChangePaymentMethod() {
        onChangePaymentMethod(false);
    }

    private void onChangePaymentMethod(final boolean fromReviewAndConfirm) {
        //TODO remove when navigation is corrected and works with stack.
        state.editPaymentMethodFromReviewAndConfirm = fromReviewAndConfirm;
        state.paymentMethodEdited = true;
        getView().showPaymentMethodSelection();
        if (fromReviewAndConfirm) {
            //Button "change payment method" in R&C
            getView().transitionOut();
        }
    }

    public void confirmCardFlow() {
        getView().showProgress();
    }

    public void cancelCardFlow() {
        getView().hideProgress();
    }

    public boolean isUniquePaymentMethod() {
        return state.isUniquePaymentMethod;
    }
}