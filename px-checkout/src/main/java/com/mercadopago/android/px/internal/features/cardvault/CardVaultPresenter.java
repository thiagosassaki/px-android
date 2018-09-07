package com.mercadopago.android.px.internal.features.cardvault;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.providers.CardVaultProvider;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.EscUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.DifferentialPricing;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
import java.util.List;

public class CardVaultPresenter extends MvpPresenter<CardVaultView, CardVaultProvider> {

    @NonNull private final AmountRepository amountRepository;
    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;

    private FailureRecovery failureRecovery;
    private String bin;

    //Activity parameters
    private PaymentRecovery paymentRecovery;

    private boolean automaticSelection;

    private String merchantBaseUrl;
    private boolean installmentsListShown;
    private boolean issuersListShown;

    //Activity result
    protected PaymentMethod paymentMethod;
    private PayerCost payerCost;

    //Card Info
    private CardInfo cardInfo;
    private Token token;
    private Card card;

    private List<PayerCost> payerCostsList;

    //Security Code
    private String esc;

    public CardVaultPresenter(@NonNull final AmountRepository amountRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository) {
        this.userSelectionRepository = userSelectionRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.amountRepository = amountRepository;
    }

    public void initialize() {
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

    private boolean viewAttached() {
        return getView() != null;
    }

    public void setPaymentRecovery(final PaymentRecovery paymentRecovery) {
        this.paymentRecovery = paymentRecovery;
    }

    public void setCard(final Card card) {
        this.card = card;
    }

    public void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
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

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(paymentMethod, bin);
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
        if (payerCost == null) {
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
        if (userSelectionRepository.getIssuer() == null) {
            issuersListShown = true;
            getView().startIssuersActivity();
        } else {
            checkStartInstallmentsActivity();
        }
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
        final String bin = TextUtil.isEmpty(cardInfo.getFirstSixDigits()) ? "" : cardInfo.getFirstSixDigits();
        final Long issuerId = this.card.getIssuer() == null ? null : this.card.getIssuer().getId();
        String paymentMethodId = card.getPaymentMethod() == null ? "" : card.getPaymentMethod().getId();
        final DifferentialPricing differentialPricing = paymentSettingRepository.getCheckoutPreference().getDifferentialPricing();
        final Integer differentialPricingId = differentialPricing == null ? null : differentialPricing.getId();
        getResourcesProvider().getInstallmentsAsync(bin, issuerId, paymentMethodId, amountRepository.getAmountToPay(),
            differentialPricingId,
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
            paymentSettingRepository.getCheckoutPreference().getPaymentPreference().getDefaultInstallments(payerCosts);
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

    public void resolveIssuersRequest() {
        issuersListShown = true;
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

    public void resolveSecurityCodeRequest() {
        setToken(paymentSettingRepository.getToken());
        if (tokenRecoveryAvailable()) {
            setPayerCost(getPaymentRecovery().getPayerCost());
        }
        finishWithResult();
    }

    public void resolveNewCardRequest(final PayerCost payerCost,
        final List<PayerCost> payerCosts) {
        setCardInfo(new CardInfo(paymentSettingRepository.getToken()));
        setPayerCost(payerCost);
        setPayerCostsList(payerCosts);
        checkStartIssuersActivity();
    }

    public void onResultCancel() {
        getView().cancelCardVault();
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
        userSelectionRepository.select(getCard().getIssuer());
        if (userSelectionRepository.getPayerCost() != null) {
            askForSecurityCodeWithoutInstallments();
        } else {
            getInstallmentsForCardAsync(getCard());
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
            
            final SavedESCCardToken escCardToken = SavedESCCardToken.createWithEsc(card.getId(), esc);

            getResourcesProvider()
                .createESCTokenAsync(escCardToken, new TaggedCallback<Token>(ApiUtil.RequestOrigin.CREATE_TOKEN) {
                    @Override
                    public void onSuccess(final Token token) {
                        CardVaultPresenter.this.token = token;
                        CardVaultPresenter.this.token.setLastFourDigits(card.getLastFourDigits());
                        paymentSettingRepository.configure(CardVaultPresenter.this.token);
                        finishWithResult();
                    }

                    @Override
                    public void onFailure(final MercadoPagoError error) {
                        if (error.isApiException() &&
                            EscUtil.isInvalidEscForApiException(error.getApiException())) {

                            getResourcesProvider().deleteESC(escCardToken.getCardId());
                            esc = null;

                            //Start CVV screen if fail
                            if (viewAttached()) {
                                getView().startSecurityCodeActivity(TrackingUtil.SECURITY_CODE_REASON_ESC);
                            }
                        } else {
                            //Retry with error screen
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
