package com.mercadopago.android.px.installments;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.mocks.Installments;
import com.mercadopago.android.px.mocks.Issuers;
import com.mercadopago.android.px.mocks.PayerCosts;
import com.mercadopago.android.px.mocks.PaymentMethods;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.internal.features.InstallmentsPresenter;
import com.mercadopago.android.px.internal.features.providers.InstallmentsProvider;
import com.mercadopago.android.px.internal.features.InstallmentsActivityView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentsPresenterTest {

    private MockedView mockedView = new MockedView();
    private MockedProvider provider = new MockedProvider();
    private InstallmentsPresenter presenter;

    @Mock private AmountRepository amountRepository;
    @Mock private PaymentSettingRepository configuration;
    @Mock private CheckoutPreference checkoutPreference;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private DiscountRepository discountRepository;

    @Before
    public void setUp() {
        //Simulation no charge - no discount
        when(checkoutPreference.getSite()).thenReturn(Sites.ARGENTINA);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(amountRepository.getAmountToPay()).thenReturn(new BigDecimal(1000));
        presenter = new InstallmentsPresenter(amountRepository, configuration, userSelectionRepository,
            discountRepository);
        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(provider);
    }

    @Test
    public void whenPayerCostIsNullThenGetInstallments() {

        final List<Installment> installments = Installments.getInstallmentsList();
        provider.setResponse(installments);

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();

        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        assertTrue(mockedView.headerShown);
        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.installmentsShown);
    }

    @Test
    public void whenGetInstallmentsGetEmptyListThenShowError() {

        final List<Installment> installments = new ArrayList<Installment>();
        provider.setResponse(installments);

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.noInstallmentsFoundErrorGotten);
    }

    @Test
    public void whenGetInstallmentsGetMoreThanOneElementsThenShowError() {

        final List<Installment> installments = getThreeInstallmentList();
        provider.setResponse(installments);

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.multipleInstallmentsErrorGotten);
    }

    @Test
    public void whenPayerCostIsNotNullThenFinishWithPayerCost() {

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        userSelectionRepository.select(paymentMethod);
        userSelectionRepository.select(issuer);
        presenter.setPayerCosts(payerCosts);

        presenter.initialize();

        assertTrue(mockedView.headerShown);
        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.installmentsShown);
    }

    @Test
    public void whenInstallmentIsSelectedFinishWithResult() {

        final List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.setCardInfo(getCardInfo());
        userSelectionRepository.select(paymentMethod);
        userSelectionRepository.select(issuer);
        presenter.setPayerCosts(payerCosts);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenSelectOnInstallmentThenFinishWithPayerCost() {

        final List<Installment> installments = Installments.getInstallmentsList();
        provider.setResponse(installments);

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();

        presenter.setPaymentPreference(paymentPreference);
        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        mockedView.simulateInstallmentSelection(0);

        assertTrue(mockedView.installmentsShown);
        assertTrue(mockedView.headerShown);
        assertTrue(mockedView.finishWithResult);
        assertEquals(installments.get(0).getPayerCosts().get(0), mockedView.selectedPayerCost);
    }

    @Test
    public void whenGetInstallmentFailThenShowError() {

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();

        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
    }

    @Test
    public void whenRecoverFromFailureThenGetInstallmentsAgain() {

        MercadoPagoError mercadoPagoError = new MercadoPagoError("Error", true);
        provider.setResponse(mercadoPagoError);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);

        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();
        presenter.recoverFromFailure();

        assertFalse(mockedView.loadingViewShown);
        assertTrue(mockedView.errorShown);
        assertNotEquals(presenter.getFailureRecovery(), null);
    }

    @Test
    public void whenRecoverFromFailureIsNullThenNotRecoverFromError() {
        presenter.recoverFromFailure();
        assertEquals(presenter.getFailureRecovery(), null);
    }

    @Test
    public void whenPayerCostsSizeIsOneThenFinishWithResult() {

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuerMLA();
        final PaymentPreference paymentPreference = new PaymentPreference();
        final List<PayerCost> payerCosts = PayerCosts.getOnePayerCostList();
        presenter.setPaymentPreference(paymentPreference);
        presenter.setCardInfo(getCardInfo());
        presenter.setPayerCosts(payerCosts);
        userSelectionRepository.select(paymentMethod);
        userSelectionRepository.select(issuer);

        presenter.initialize();

        assertEquals(mockedView.selectedPayerCost, payerCosts.get(0));
        assertTrue(mockedView.finishWithResult);
    }

    @Test
    public void whenPayerCostsisEmptyhenShowError() {

        List<Installment> installments = Installments.getInstallmentsListWithoutPayerCosts();
        provider.setResponse(installments);

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        presenter.setPaymentPreference(paymentPreference);
        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);

        presenter.initialize();

        assertTrue(mockedView.errorShown);
        assertTrue(provider.noPayerCostFoundErrorGotten);
    }

    @Test
    public void whenPaymentPreferenceHasDefaultPayerCostThenFinishWithResult() {

        List<PayerCost> payerCosts = PayerCosts.getPayerCostsWithCFT();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuerMLA();
        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setDefaultInstallments(1);
        presenter.setPaymentPreference(paymentPreference);
        presenter.setCardInfo(getCardInfo());
        presenter.setPayerCosts(payerCosts);
        userSelectionRepository.select(paymentMethod);
        userSelectionRepository.select(issuer);

        presenter.initialize();

        assertTrue(mockedView.finishWithResult);
        assertEquals(mockedView.selectedPayerCost,
            getPayerCost(payerCosts, paymentPreference.getDefaultInstallments()));
    }

    @Test
    public void whenIsCardInfoAndPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        final CardInfo cardInfo = getCardInfo();
        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        presenter.setCardInfo(cardInfo);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        assertTrue(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotPaymentMethodAvailableThenIsNotRequiredCardDrawn() {
        CardInfo cardInfo = getCardInfo();
        presenter.setCardInfo(cardInfo);
        assertFalse(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenIsNotCardInfoAvailableThenIsNotRequiredCardDrawn() {
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        userSelectionRepository.select(paymentMethod);
        assertFalse(presenter.isRequiredCardDrawn());
    }

    @Test
    public void whenCardInfoNullThenBinIsNull() {
        CardInfo cardInfo = null;
        presenter.setCardInfo(cardInfo);
        assertTrue(TextUtil.isEmpty(presenter.getBin()));
    }

    @Test
    public void whenMCOThenShowBankInterestsNotCoveredWarning() {

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        when(checkoutPreference.getSite()).thenReturn(Sites.COLOMBIA);
        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        presenter.initialize();
        assertTrue(mockedView.bankInterestsWarningShown);
    }

    @Test
    public void whenNotMCOThenDoNotShowBankInterestsNotCoveredWarning() {

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        presenter.setCardInfo(getCardInfo());
        when(userSelectionRepository.getIssuer()).thenReturn(issuer);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        presenter.initialize();
        assertFalse(mockedView.bankInterestsWarningShown);
    }

    private CardInfo getCardInfo() {
        Card card = new Card();
        card.setLastFourDigits("4321");
        card.setFirstSixDigits("123456");

        return new CardInfo(card);
    }

    private List<Installment> getThreeInstallmentList() {
        List<Installment> installments = new ArrayList<Installment>();

        Installment installment = new Installment();
        installments.add(installment);
        installments.add(installment);
        installments.add(installment);

        return installments;
    }

    private PayerCost getPayerCost(List<PayerCost> payerCosts, Integer defaultInstallments) {
        PayerCost payerCost = new PayerCost();

        for (PayerCost currentPayerCost : payerCosts) {
            if (defaultInstallments.equals(currentPayerCost.getInstallments())) {
                payerCost = currentPayerCost;
            }
        }

        return payerCost;
    }

    private class MockedProvider implements InstallmentsProvider {

        private boolean shouldFail;
        private List<Installment> successfulResponse;
        private MercadoPagoError failedResponse;

        private boolean noInstallmentsFoundErrorGotten = false;
        private boolean noPayerCostFoundErrorGotten = false;
        private boolean multipleInstallmentsErrorGotten = false;

        MockedProvider() {
            successfulResponse = Installments.getInstallmentsList();
            failedResponse = new MercadoPagoError("Default mocked error", false);
        }

        void setResponse(List<Installment> installments) {
            shouldFail = false;
            successfulResponse = installments;
        }

        private void setResponse(MercadoPagoError exception) {
            shouldFail = true;
            failedResponse = exception;
        }

        @Override
        public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId,
            Integer differential,
            TaggedCallback<List<Installment>> taggedCallback) {
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(successfulResponse);
            }
        }

        @Override
        public MercadoPagoError getNoInstallmentsFoundError() {
            this.noInstallmentsFoundErrorGotten = true;
            return null;
        }

        @Override
        public MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError() {
            this.multipleInstallmentsErrorGotten = true;
            return null;
        }

        @Override
        public MercadoPagoError getNoPayerCostFoundError() {
            this.noPayerCostFoundErrorGotten = true;
            return null;
        }
    }

    private class MockedView implements InstallmentsActivityView {

        private boolean installmentsShown = false;
        private boolean finishWithResult = false;
        private boolean headerShown = false;
        private boolean errorShown = false;
        private boolean loadingViewShown = false;
        private boolean installmentRecyclerViewShown = false;
        private PayerCost selectedPayerCost;
        private OnSelectedCallback<Integer> installmentSelectionCallback;
        private boolean bankInterestsWarningShown = false;

        @Override
        public void showInstallments(List<PayerCost> payerCostList, OnSelectedCallback<Integer> onSelectedCallback) {
            this.installmentSelectionCallback = onSelectedCallback;
            this.installmentsShown = true;
        }

        @Override
        public void finishWithResult(PayerCost payerCost) {
            this.finishWithResult = true;
            this.selectedPayerCost = payerCost;
        }

        @Override
        public void showLoadingView() {
            this.loadingViewShown = true;
        }

        @Override
        public void hideLoadingView() {
            this.loadingViewShown = false;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void showHeader() {
            this.headerShown = true;
        }

        @Override
        public void showInstallmentsRecyclerView() {
            this.installmentRecyclerViewShown = true;
        }

        @Override
        public void warnAboutBankInterests() {
            bankInterestsWarningShown = true;
        }

        @Override
        public void showDetailDialog() {
            // do nothing
        }

        @Override
        public void showDiscountInputDialog() {
            // do nothing
        }

        @Override
        public void showAmount(@NonNull final DiscountRepository discountRepository,
            @NonNull final BigDecimal itemsPlusCharges, @NonNull final Site site) {
            // do nothing
        }

        @Override
        public void onSuccessCodeDiscountCallback(Discount discount) {
            // do nothing
        }

        @Override
        public void onFailureCodeDiscountCallback() {
            // do nothing
        }

        private void simulateInstallmentSelection(int index) {
            installmentSelectionCallback.onSelected(index);
        }
    }
}
