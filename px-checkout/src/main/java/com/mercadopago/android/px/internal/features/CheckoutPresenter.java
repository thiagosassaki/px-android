package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandler;
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
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.internal.viewmodel.OneTapModel;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.Issuer;
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

public class CheckoutPresenter extends MvpPresenter<CheckoutView, CheckoutProvider> implements PaymentServiceHandler {

    private static final String TAG = CheckoutPresenter.class.getName();

    @NonNull private final CheckoutStateModel state;

    @NonNull private final PluginRepository pluginRepository;
    @NonNull private final PaymentRepository paymentRepository;

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

    private transient FailureRecovery failureRecovery;

    private PluginInitializationTask pluginInitializationTask; //instance saved as attribute to cancel and avoid crash

    public CheckoutPresenter(@NonNull final CheckoutStateModel persistentData,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final GroupsRepository groupsRepository,
        @NonNull final PluginRepository pluginRepository,
        @NonNull final PaymentRepository paymentRepository) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.amountRepository = amountRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
        this.groupsRepository = groupsRepository;
        this.pluginRepository = pluginRepository;
        this.paymentRepository = paymentRepository;
        state = persistentData;
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
        initializePluginsData();
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
        final Card card) {
        state.createdToken = token;
        state.selectedCard = card;

        onPaymentMethodSelected();
    }

    private void onPaymentMethodSelected() {
        if (!showHook2(paymentRepository.getPaymentData())) {
            hook2Continue();
        }
    }

    public void createPayment() {
        getView().showProgress();
        paymentRepository.startPayment(this);
    }

    private void continuePaymentWithoutESC() {
        state.paymentRecovery =
            new PaymentRecovery(paymentSettingRepository.getToken(), userSelectionRepository.getPaymentMethod(),
                userSelectionRepository.getPayerCost(), userSelectionRepository.getIssuer(),
                Payment.StatusCodes.STATUS_REJECTED,
                Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);

        getView().startPaymentRecoveryFlow(state.paymentRecovery);
    }

    private void resolvePaymentFailure(final MercadoPagoError mercadoPagoError) {

        if (mercadoPagoError != null) {
            if (mercadoPagoError.isPaymentProcessing()) {
                final PaymentResult paymentResult =
                    new PaymentResult.Builder()
                        .setPaymentData(paymentRepository.getPaymentData())
                        .setPaymentStatus(Payment.StatusCodes.STATUS_IN_PROCESS)
                        .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY)
                        .build();
                getView()
                    .showPaymentResult(paymentResult, amountRepository.getAmountToPay(), discountRepository.getDiscount());
            } else if (mercadoPagoError.isInternalServerError()) {
                resolveInternalServerError(mercadoPagoError);
            } else if (mercadoPagoError.isBadRequestError()) {
                resolveBadRequestError(mercadoPagoError);
            } else {
                getView().showError(mercadoPagoError);
            }
        } else {
            getView().showError(mercadoPagoError);
        }

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
        if (!showHook3(paymentRepository.getPaymentData())) {
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

    public CheckoutPreference getCheckoutPreference() {
        return paymentSettingRepository.getCheckoutPreference();
    }

    public Campaign getCampaign() {
        return discountRepository.getCampaign();
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

    private void finishCheckout() {
        //TODO improve this
        if (state.createdPayment != null && state.createdPayment instanceof Payment) {
            getView().finishWithPaymentResult((Payment) state.createdPayment);
        } else {
            getView().finishWithPaymentResult();
        }
    }

    public void onCustomPaymentResultResponse(final Integer customResultCode) {
        //TODO improve this
        if (state.createdPayment != null && state.createdPayment instanceof Payment) {
            getView().finishWithPaymentResult(customResultCode, (Payment) state.createdPayment);
        } else {
            getView().finishWithPaymentResult(customResultCode);
        }
    }

    private void recoverPayment() {
        try {
            state.paymentRecovery =
                new PaymentRecovery(paymentSettingRepository.getToken(),
                    userSelectionRepository.getPaymentMethod(),
                    userSelectionRepository.getPayerCost(),
                    userSelectionRepository.getIssuer(), state.createdPayment.getPaymentStatus(),
                    state.createdPayment.getPaymentStatusDetail());
            getView().startPaymentRecoveryFlow(state.paymentRecovery);
        } catch (final Exception e) {
            final String message = getResourcesProvider().getCheckoutExceptionMessage(e);
            getView().showError(new MercadoPagoError(message, e.getMessage(), false));
        }
    }

    /**
     * Send intention to close checkout
     * if the checkout has oneTap data then it should not close.
     */
    public void cancelCheckout() {
        //TODO improve this
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

    public boolean isUniquePaymentMethod() {
        return state.isUniquePaymentMethod;
    }

    @Override
    public void onCvvRequired(@NonNull final Card card) {
        //TODO check.
        if (isViewAttached()) {
            getView().showSavedCardFlow(card);
        }
    }

    @Override
    public void onVisualPayment() {
        if (isViewAttached()) {
            getView().showPaymentProcessor();
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final Payment payment) {
        if (isViewAttached()) {
            getView().hideProgress();
            state.createdPayment = payment;
            getView()
                .showPaymentResult(paymentRepository.createPaymentResult(payment),
                    amountRepository.getAmountToPay(),
                    discountRepository.getDiscount());
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final GenericPayment genericPayment) {
        if (isViewAttached()) {
            getView().hideProgress();
            state.createdPayment = genericPayment;
            getView()
                .showPaymentResult(paymentRepository.createPaymentResult(genericPayment),
                    amountRepository.getAmountToPay(),
                    discountRepository.getDiscount());
        }
    }

    @Override
    public void onPaymentFinished(@NonNull final BusinessPayment businessPayment) {
        if (isViewAttached()) {
            getView().hideProgress();
            state.createdPayment = businessPayment;
            getView().showBusinessResult(
                new BusinessModelMapper(discountRepository, paymentSettingRepository, amountRepository,
                    paymentRepository)
                    .map(businessPayment));
        }
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        if (isViewAttached()) {
            getView().hideProgress();
            recoverCreatePayment(error);
        }
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

    @Override
    public void onRecoverPaymentEscInvalid() {
        if (isViewAttached()) {
            continuePaymentWithoutESC();
        }
    }
}