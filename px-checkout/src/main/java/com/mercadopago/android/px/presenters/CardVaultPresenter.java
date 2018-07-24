package com.mercadopago.android.px.presenters;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.callbacks.FailureRecovery;
import com.mercadopago.android.px.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.mvp.MvpPresenter;
import com.mercadopago.android.px.mvp.TaggedCallback;
import com.mercadopago.android.px.providers.CardVaultProvider;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.tracking.utils.TrackingUtil;
import com.mercadopago.android.px.util.ApiUtil;
import com.mercadopago.android.px.util.TextUtils;
import com.mercadopago.android.px.views.CardVaultView;
import java.util.List;

public class CardVaultPresenter extends MvpPresenter<CardVaultView, CardVaultProvider> {

    @NonNull
    private final AmountRepository amountRepository;
    private final PaymentSettingRepository configuration;
    @NonNull private final UserSelectionRepository userSelectionRepository;

    private FailureRecovery failureRecovery;
    private String bin;

    //Activity parameters
    private PaymentRecovery paymentRecovery;

    private boolean installmentsEnabled;
    private boolean installmentsReviewEnabled;
    private boolean automaticSelection;

    private String merchantBaseUrl;
    private boolean installmentsListShown;
    private boolean issuersListShown;

    //Activity result
    protected PaymentMethod paymentMethod;
    private PayerCost payerCost;
    private Issuer issuer;

    //Card Info
    private CardInfo cardInfo;
    private Token token;
    private Card card;

    private List<PayerCost> payerCostsList;
    private List<Issuer> issuersList;

    //Security Code
    private String esc;
    private SavedESCCardToken escCardToken;

    public CardVaultPresenter(@NonNull final AmountRepository amountRepository,
        @NonNull final PaymentSettingRepository configuration,
        @NonNull final UserSelectionRepository userSelectionRepository) {
        this.configuration = configuration;
        this.userSelectionRepository = userSelectionRepository;
        installmentsEnabled = true;
        this.amountRepository = amountRepository;
    }

    public void initialize() {
        try {
            onValidStart();
        } catch (final IllegalStateException exception) {
            getView().showError(new MercadoPagoError(exception.getMessage(), false), "");
        }
    }

    private boolean viewAttached() {
        return getView() != null;
    }

    public void setPaymentRecovery(final PaymentRecovery paymentRecovery) {
        this.paymentRecovery = paymentRecovery;
    }

    public void setInstallmentsEnabled(final boolean installmentsEnabled) {
        this.installmentsEnabled = installmentsEnabled;
    }

    public void setCard(final Card card) {
        this.card = card;
    }

    public void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(final Issuer mIssuer) {
        this.issuer = mIssuer;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(final Token mToken) {
        this.token = mToken;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod mPaymentMethod) {
        this.paymentMethod = mPaymentMethod;
    }

    public PayerCost getPayerCost() {
        return payerCost;
    }

    public void setPayerCost(final PayerCost mPayerCost) {
        this.payerCost = mPayerCost;
    }

    public PaymentRecovery getPaymentRecovery() {
        return paymentRecovery;
    }

    public Card getCard() {
        return card;
    }

    public void setESC(final String esc) {
        this.esc = esc;
    }

    public void setCardInfo(final CardInfo cardInfo) {
        this.cardInfo = cardInfo;
        if (this.cardInfo == null) {
            bin = "";
        } else {
            bin = this.cardInfo.getFirstSixDigits();
        }
    }

    public void setInstallmentsReviewEnabled(final boolean installmentReviewEnabled) {
        installmentsReviewEnabled = installmentReviewEnabled;
    }

    public Boolean getInstallmentsReviewEnabled() {
        return installmentsReviewEnabled;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(paymentMethod, bin);
    }

    public void setMerchantBaseUrl(final String merchantBaseUrl) {
        this.merchantBaseUrl = merchantBaseUrl;
    }

    public String getMerchantBaseUrl() {
        return merchantBaseUrl;
    }

    public void setAutomaticSelection(final boolean automaticSelection) {
        this.automaticSelection = automaticSelection;
    }

    public boolean isInstallmentsListShown() {
        return installmentsListShown;
    }

    public boolean isIssuersListShown() {
        return issuersListShown;
    }

    public void setInstallmentsListShown(final boolean installmentsListShown) {
        this.installmentsListShown = installmentsListShown;
    }

    public void setIssuersListShown(final boolean issuersListShown) {
        this.issuersListShown = issuersListShown;
    }

    private void checkStartInstallmentsActivity() {
        if (isInstallmentsEnabled() && payerCost == null) {
            installmentsListShown = true;
            askForInstallments();
        } else {
            finishWithResult();
        }
    }

    private void finishWithResult() {
        if (isSecurityCodeFlowNeeded()) {
            getView().animateTransitionSlideInSlideOut();
        } else {
            getView().transitionWithNoAnimation();
        }
        getView().finishWithResult();
    }

    private void askForInstallments() {
        if (issuersListShown) {
            getView().askForInstallmentsFromIssuers();
        } else if (!savedCardAvailable()) {
            getView().askForInstallmentsFromNewCard();
        } else {
            getView().askForInstallments();
        }
    }

    private void checkStartIssuersActivity() {
        if (issuer == null) {
            issuersListShown = true;
            getView().startIssuersActivity();
        } else {
            checkStartInstallmentsActivity();
        }
    }

    public boolean isInstallmentsEnabled() {
        return installmentsEnabled;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    public List<PayerCost> getPayerCostList() {
        return payerCostsList;
    }

    private void getInstallmentsForCardAsync(final Card card) {
        String bin = TextUtils.isEmpty(cardInfo.getFirstSixDigits()) ? "" : cardInfo.getFirstSixDigits();
        Long issuerId = this.card.getIssuer() == null ? null : this.card.getIssuer().getId();
        String paymentMethodId = card.getPaymentMethod() == null ? "" : card.getPaymentMethod().getId();

        getResourcesProvider().getInstallmentsAsync(bin, issuerId, paymentMethodId, amountRepository.getAmountToPay(),
            new TaggedCallback<List<Installment>>(ApiUtil.RequestOrigin.GET_INSTALLMENTS) {
                @Override
                public void onSuccess(final List<Installment> installments) {
                    if (viewAttached()) {
                        resolveInstallmentsList(installments);
                    }
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    if (viewAttached()) {
                        getView().showError(error, ApiUtil.RequestOrigin.GET_INSTALLMENTS);

                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getInstallmentsForCardAsync(card);
                            }
                        });
                    }
                }
            });
    }

    private void resolveInstallmentsList(final List<Installment> installments) {
        String errorMessage = null;
        if (installments.size() == 0) {
            errorMessage = getResourcesProvider().getMissingInstallmentsForIssuerErrorMessage();
        } else if (installments.size() == 1) {
            resolvePayerCosts(installments.get(0).getPayerCosts());
        } else {
            errorMessage = getResourcesProvider().getMultipleInstallmentsForIssuerErrorMessage();
        }
        if (errorMessage != null && isViewAttached()) {
            getView().showError(new MercadoPagoError(errorMessage, false), "");
        }
    }

    private void resolvePayerCosts(final List<PayerCost> payerCosts) {
        final PayerCost defaultPayerCost =
            configuration.getCheckoutPreference().getPaymentPreference().getDefaultInstallments(payerCosts);
        payerCostsList = payerCosts;

        if (defaultPayerCost != null) {
            userSelectionRepository.select(defaultPayerCost);
            payerCost = defaultPayerCost;
            askForSecurityCodeWithoutInstallments();
        } else if (payerCostsList.isEmpty()) {
            getView()
                .showError(new MercadoPagoError(getResourcesProvider().getMissingPayerCostsErrorMessage(), false), "");
        } else if (payerCostsList.size() == 1) {
            userSelectionRepository.select(payerCosts.get(0));
            payerCost = payerCosts.get(0);
            askForSecurityCodeWithoutInstallments();
        } else {
            installmentsListShown = true;
            getView().askForInstallments();
        }
    }

    private void askForSecurityCodeWithoutInstallments() {
        if (isSecurityCodeFlowNeeded()) {
            getView().animateTransitionSlideInSlideOut();
        } else {
            getView().transitionWithNoAnimation();
        }
        startSecurityCodeFlowIfNeeded();
    }

    private void askForSecurityCodeFromInstallments() {
        getView().animateTransitionSlideInSlideOut();
        startSecurityCodeFlowIfNeeded();
    }

    public void resolveIssuersRequest(final Issuer issuer) {
        issuersListShown = true;
        setIssuer(issuer);
        checkStartInstallmentsActivity();
    }

    public void resolveInstallmentsRequest(final PayerCost payerCost) {
        setSelectedInstallments(payerCost);

        if (savedCardAvailable()) {
            if (installmentsListShown) {
                askForSecurityCodeFromInstallments();
            } else {
                askForSecurityCodeWithoutInstallments();
            }
        } else {
            finishWithResult();
        }
    }

    private void setSelectedInstallments(final PayerCost payerCost) {
        installmentsListShown = true;
        setPayerCost(payerCost);
    }

    public void resolveSecurityCodeRequest(final Token token) {
        setToken(token);
        if (tokenRecoveryAvailable()) {
            setPayerCost(getPaymentRecovery().getPayerCost());
            setIssuer(getPaymentRecovery().getIssuer());
        }
        finishWithResult();
    }

    public void resolveNewCardRequest(final PaymentMethod paymentMethod, final Token token,
        final PayerCost payerCost, final Issuer issuer,
        final List<PayerCost> payerCosts, final List<Issuer> issuers) {

        setPaymentMethod(paymentMethod);
        setToken(token);
        setCardInfo(new CardInfo(token));
        setPayerCost(payerCost);
        setIssuer(issuer);
        setPayerCostsList(payerCosts);
        setIssuersList(issuers);
        checkStartIssuersActivity();
    }

    public void onResultCancel() {
        getView().cancelCardVault();
    }

    private void onValidStart() {
        installmentsListShown = false;
        issuersListShown = false;
        if (viewAttached()) {
            getView().showProgressLayout();
        }
        if (tokenRecoveryAvailable()) {
            startTokenRecoveryFlow();
        } else if (savedCardAvailable()) {
            startSavedCardFlow();
        } else {
            startNewCardFlow();
        }
    }

    private void startTokenRecoveryFlow() {
        setCardInfo(new CardInfo(getPaymentRecovery().getToken()));
        setPaymentMethod(getPaymentRecovery().getPaymentMethod());
        setToken(getPaymentRecovery().getToken());
        getView().askForSecurityCodeFromTokenRecovery();
    }

    private void startSavedCardFlow() {
        setCardInfo(new CardInfo(getCard()));
        setPaymentMethod(getCard().getPaymentMethod());
        setIssuer(getCard().getIssuer());
        if (isInstallmentsEnabled()) {
            getInstallmentsForCardAsync(getCard());
        } else {
            askForSecurityCodeWithoutInstallments();
        }
    }

    private void startNewCardFlow() {
        getView().askForCardInformation();
    }

    private boolean tokenRecoveryAvailable() {
        return getPaymentRecovery() != null && getPaymentRecovery().isTokenRecoverable();
    }

    private boolean savedCardAvailable() {
        return getCard() != null;
    }

    public void setPayerCostsList(final List<PayerCost> payerCostsList) {
        this.payerCostsList = payerCostsList;
    }

    private void setIssuersList(final List<Issuer> issuers) {
        issuersList = issuers;
    }

    public List<Issuer> getIssuersList() {
        return issuersList;
    }

    public void startSecurityCodeFlowIfNeeded() {
        if (isSecurityCodeFlowNeeded()) {
            getView().startSecurityCodeActivity(TrackingUtil.SECURITY_CODE_REASON_SAVED_CARD);
        } else {
            createESCToken();
        }
    }

    public boolean isSecurityCodeFlowNeeded() {
        return !savedCardAvailable() || !isESCSaved();
    }

    private boolean isESCSaved() {
        if (!isESCEmpty()) {
            return true;
        } else {
            setESC(getResourcesProvider().findESCSaved(card.getId()));
            return !isESCEmpty();
        }
    }

    private boolean isESCEmpty() {
        return esc == null || esc.isEmpty();
    }

    private void createESCToken() {
        if (savedCardAvailable() && !isESCEmpty()) {

            escCardToken = SavedESCCardToken.createWithEsc(card.getId(), esc);

            getResourcesProvider()
                .createESCTokenAsync(escCardToken, new TaggedCallback<Token>(ApiUtil.RequestOrigin.CREATE_TOKEN) {
                    @Override
                    public void onSuccess(final Token token) {
                        CardVaultPresenter.this.token = token;
                        CardVaultPresenter.this.token.setLastFourDigits(card.getLastFourDigits());
                        finishWithResult();
                    }

                    @Override
                    public void onFailure(final MercadoPagoError error) {

                        if (error.isApiException() &&
                            error.getApiException().getStatus() == ApiUtil.StatusCodes.BAD_REQUEST) {
                            List<Cause> causes = error.getApiException().getCause();
                            if (causes != null && !causes.isEmpty()) {
                                Cause cause = causes.get(0);
                                if (ApiException.ErrorCodes.INVALID_ESC.equals(cause.getCode()) ||
                                    ApiException.ErrorCodes.INVALID_FINGERPRINT.equals(cause.getCode())) {

                                    getResourcesProvider().deleteESC(escCardToken.getCardId());

                                    esc = null;
                                    if (viewAttached()) {
                                        getView().startSecurityCodeActivity(TrackingUtil.SECURITY_CODE_REASON_ESC);
                                    }
                                } else {
                                    recoverCreateESCToken(error);
                                }
                            }
                        } else {
                            recoverCreateESCToken(error);
                        }
                    }
                });
        }
    }

    private void recoverCreateESCToken(final MercadoPagoError error) {
        if (viewAttached()) {
            getView().showError(error, ApiUtil.RequestOrigin.CREATE_TOKEN);
            setFailureRecovery(new FailureRecovery() {
                @Override
                public void recover() {
                    createESCToken();
                }
            });
        }
    }
}
