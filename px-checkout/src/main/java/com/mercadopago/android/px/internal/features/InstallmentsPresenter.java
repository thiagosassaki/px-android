package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.internal.features.providers.InstallmentsProvider;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.InstallmentsUtil;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.DifferentialPricing;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.util.List;

public class InstallmentsPresenter extends MvpPresenter<InstallmentsActivityView, InstallmentsProvider> implements
    AmountView.OnClick {

    @NonNull
    private final AmountRepository amountRepository;
    @NonNull
    private final PaymentSettingRepository configuration;
    @NonNull
    private final UserSelectionRepository userSelectionRepository;
    @NonNull
    private final DiscountRepository discountRepository;

    private FailureRecovery mFailureRecovery;

    //Card Info
    private String bin = "";

    private List<PayerCost> payerCosts;
    private PaymentPreference paymentPreference;
    private CardInfo cardInfo;

    public InstallmentsPresenter(@NonNull final AmountRepository amountRepository,
        @NonNull final PaymentSettingRepository configuration,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DiscountRepository discountRepository) {
        this.amountRepository = amountRepository;
        this.configuration = configuration;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
    }

    public void initialize() {
        initializeAmountRow();
        showSiteRelatedInformation();
        loadPayerCosts();
    }

    public void initializeAmountRow() {
        if (isViewAttached()) {
            getView().showAmount(discountRepository,
                amountRepository.getItemsPlusCharges(), configuration.getCheckoutPreference().getSite());
        }
    }

    private void showSiteRelatedInformation() {
        if (InstallmentsUtil.shouldWarnAboutBankInterests(configuration.getCheckoutPreference().getSite().getId())) {
            getView().warnAboutBankInterests();
        }
    }

    private void loadPayerCosts() {
        if (werePayerCostsSet()) {
            resolvePayerCosts(payerCosts);
        } else {
            getInstallmentsAsync();
        }
    }

    private boolean werePayerCostsSet() {
        return payerCosts != null;
    }

    private void resolvePayerCosts(List<PayerCost> payerCosts) {
        final PayerCost defaultPayerCost =
            paymentPreference == null ? null : paymentPreference.getDefaultInstallments(payerCosts);
        this.payerCosts =
            paymentPreference == null ? payerCosts : paymentPreference.getInstallmentsBelowMax(payerCosts);

        if (defaultPayerCost != null) {
            userSelectionRepository.select(defaultPayerCost);
            getView().finishWithResult(defaultPayerCost);
        } else if (this.payerCosts.isEmpty()) {
            getView().showError(getResourcesProvider().getNoPayerCostFoundError(), "");
        } else if (this.payerCosts.size() == 1) {
            final PayerCost payerCost = payerCosts.get(0);
            userSelectionRepository.select(payerCost);
            getView().finishWithResult(payerCost);
        } else {
            getView().showHeader();
            getView().showInstallments(this.payerCosts, getDpadSelectionCallback());
            getView().hideLoadingView();
        }
    }

    private void getInstallmentsAsync() {
        getView().showLoadingView();
        final DifferentialPricing differentialPricing = configuration.getCheckoutPreference().getDifferentialPricing();
        final Integer differentialPricingId = differentialPricing == null ? null : differentialPricing.getId();
        getResourcesProvider()
            .getInstallments(bin, amountRepository.getAmountToPay(), userSelectionRepository.getIssuer().getId(),
            userSelectionRepository.getPaymentMethod().getId(),
                differentialPricingId, new TaggedCallback<List<Installment>>(ApiUtil.RequestOrigin.GET_INSTALLMENTS) {
                @Override
                public void onSuccess(final List<Installment> installments) {
                    if (installments.size() == 0) {
                        getView().showError(getResourcesProvider().getNoInstallmentsFoundError(), "");
                    } else if (installments.size() == 1) {
                        resolvePayerCosts(installments.get(0).getPayerCosts());
                        getView().onSuccessCodeDiscountCallback(discountRepository.getDiscount());
                    } else {
                        getView().showError(getResourcesProvider().getMultipleInstallmentsFoundForAnIssuerError(), "");
                    }
                }

                    @Override
                    public void onFailure(final MercadoPagoError mercadoPagoError) {
                        getView().hideLoadingView();
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getInstallmentsAsync();
                            }
                        });
                        getView().showError(mercadoPagoError, ApiUtil.RequestOrigin.GET_INSTALLMENTS);
                        getView().onFailureCodeDiscountCallback();
                    }
                });
    }

    public void setCardInfo(final CardInfo cardInfo) {
        this.cardInfo = cardInfo;
        if (this.cardInfo != null) {
            bin = this.cardInfo.getFirstSixDigits();
        }
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public Integer getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(userSelectionRepository.getPaymentMethod(), bin);
    }

    public void setPayerCosts(List<PayerCost> payerCosts) {
        this.payerCosts = payerCosts;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public PaymentMethod getPaymentMethod() {
        return userSelectionRepository.getPaymentMethod();
    }

    public boolean isRequiredCardDrawn() {
        return cardInfo != null && userSelectionRepository.getPaymentMethod() != null;
    }

    private OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                onItemSelected(position);
            }
        };
    }

    public String getBin() {
        return bin;
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void onItemSelected(final int position) {
        final PayerCost selectedPayerCost = payerCosts.get(position);
        userSelectionRepository.select(selectedPayerCost);
        getView().finishWithResult(selectedPayerCost);
    }

    @Override
    public void onDetailClicked() {
        getView().showDetailDialog();
    }

    @Override
    public void onInputRequestClicked() {
        getView().showDiscountInputDialog();
    }

    public void onDiscountRetrieved() {
        getInstallmentsAsync();
        initializeAmountRow();
    }
}
