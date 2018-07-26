package com.mercadopago.android.px.presenters;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.hooks.Hook;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.mocks.Cards;
import com.mercadopago.android.px.mocks.Customers;
import com.mercadopago.android.px.mocks.Discounts;
import com.mercadopago.android.px.mocks.Installments;
import com.mercadopago.android.px.mocks.Issuers;
import com.mercadopago.android.px.mocks.PaymentMethodSearchs;
import com.mercadopago.android.px.mocks.PaymentMethods;
import com.mercadopago.android.px.mocks.Payments;
import com.mercadopago.android.px.mocks.Tokens;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Customer;
import com.mercadopago.android.px.model.Identification;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.mvp.TaggedCallback;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.BusinessPaymentModel;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.providers.CheckoutProvider;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.services.exceptions.CheckoutPreferenceException;
import com.mercadopago.android.px.util.TextUtils;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import com.mercadopago.android.px.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.views.CheckoutView;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE;
import static com.mercadopago.android.px.core.MercadoPagoCheckout.PAYMENT_RESULT_CODE;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubExpiredPreference;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceOneItem;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceOneItemAndPayer;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceWithAccessToken;
import static com.mercadopago.android.px.utils.StubPaymentResult.stubApprovedOffPaymentResult;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPresenterTest {

    private static final String PREF_ID = "123";

    private static final int REQUESTED_RESULT = 0x20;

    @Mock private MercadoPagoCheckout mercadoPagoCheckout;
    @Mock private CheckoutView checkoutView;
    @Mock private CheckoutProvider checkoutProvider;
    @Mock private PaymentSettingRepository configuration;
    @Mock private AmountRepository amountRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private PaymentMethod paymentMethod;
    @Mock private GroupsRepository groupsRepository;

    private MockedView view;
    private MockedProvider provider;

    @Before
    public void setUp() {
        view = new MockedView();
        provider = new MockedProvider();
        when(discountRepository.configureDiscountAutomatically(amountRepository.getAmountToPay()))
            .thenReturn(new StubSuccessMpCall<>(true));
    }

    @NonNull
    private CheckoutPresenter getPresenter(final int resultCode) {
        return getBasePresenter(resultCode, checkoutView, checkoutProvider);
    }

    @NonNull
    private CheckoutPresenter getPaymentPresenterWithDefaultAdvancedConfigurationMla() {
        final CheckoutPreference preference = stubPreferenceOneItem();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        provider.setCheckoutPreferenceResponse(preference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        return getBasePresenter(PAYMENT_RESULT_CODE, view, provider);
    }

    @NonNull
    private CheckoutPresenter getPaymentPresenter() {
        final CheckoutPreference preference = stubPreferenceOneItem();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        return getBasePresenter(PAYMENT_RESULT_CODE, view, provider);
    }

    @NonNull
    private CheckoutPresenter getBasePresenter(final int resultCode,
        final CheckoutView view,
        final CheckoutProvider provider) {
        final CheckoutStateModel model = new CheckoutStateModel(resultCode, mercadoPagoCheckout);
        final CheckoutPresenter presenter = new CheckoutPresenter(model, configuration, amountRepository,
            userSelectionRepository, discountRepository,
            groupsRepository);
        presenter.attachResourcesProvider(provider);
        presenter.attachView(view);
        return presenter;
    }

    @Test
    public void whenResolvePaymentErrorVerifyEscManagerCalled() {
        final PaymentData paymentData = mock(PaymentData.class);
        final MercadoPagoError error = mock(MercadoPagoError.class);
        when(checkoutProvider.manageEscForError(error, paymentData)).thenReturn(false);
        final CheckoutPresenter presenter = getPresenter(PAYMENT_RESULT_CODE);
        presenter.resolvePaymentError(error, paymentData);
        verify(checkoutProvider).manageEscForError(error, paymentData);
    }

    @Test
    public void whenResolvePaymentErrorEscWasInvalidatedVerifyEscManagerCalledAndRecoveryFlowStarted() {
        when(configuration.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        final PaymentData paymentData = mock(PaymentData.class);
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final Token token = mock(Token.class);
        final Issuer issuer = mock(Issuer.class);
        final PayerCost payerCost = mock(PayerCost.class);
        final PaymentMethod paymentMethod = mock(PaymentMethod.class);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);

        when(checkoutProvider.manageEscForError(error, paymentData)).thenReturn(true);
        final CheckoutPresenter presenter = getPresenter(PAYMENT_RESULT_CODE);

        presenter.onCardFlowResponse(issuer, token);

        verify(checkoutView).showReviewAndConfirm(false);

        presenter.resolvePaymentError(error, paymentData);

        verify(checkoutProvider).manageEscForError(error, paymentData);

        verify(checkoutView).startPaymentRecoveryFlow(any(PaymentRecovery.class));

        verifyNoMoreInteractions(checkoutProvider);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenShouldShowPaymentResultVerifyEscManagerCalled() {
        final CheckoutPresenter presenter = getPresenter(PAYMENT_RESULT_CODE);
        final PaymentResult paymentResult = mock(PaymentResult.class);
        final PaymentData paymentData = mock(PaymentData.class);
        when(paymentResult.getPaymentData()).thenReturn(paymentData);
        when(paymentResult.getPaymentStatus()).thenReturn(Payment.StatusCodes.STATUS_APPROVED);
        when(paymentResult.getPaymentStatusDetail()).thenReturn(Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);

        when(checkoutProvider.manageEscForPayment(paymentData,
            paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())).thenReturn(false);

        presenter.checkStartPaymentResultActivity(paymentResult);

        verify(checkoutProvider).manageEscForPayment(paymentData,
            paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail());

        verifyNoMoreInteractions(checkoutProvider);
    }

    @Test
    public void whenShouldShowBusinessPaymentVerifyEscManagerCalled() {
        final CheckoutPresenter presenter = getPresenter(PAYMENT_RESULT_CODE);
        when(configuration.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        final BusinessPayment paymentResult = mock(BusinessPayment.class);
        final PaymentData paymentData = mock(PaymentData.class);
        CheckoutStore.getInstance().setPaymentData(paymentData);
        when(paymentResult.getPaymentStatus()).thenReturn(Payment.StatusCodes.STATUS_APPROVED);
        when(paymentResult.getPaymentStatusDetail()).thenReturn(Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);

        when(checkoutProvider.manageEscForPayment(paymentData,
            paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())).thenReturn(false);

        presenter.onBusinessResult(paymentResult);

        verify(checkoutProvider).manageEscForPayment(paymentData,
            paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail());

        verifyNoMoreInteractions(checkoutProvider);
    }

    @Ignore
    @Test
    public void onCreatePaymentWithESCTokenErrorThenRequestSecurityCode() {

        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        ApiException apiException = Payments.getInvalidESCPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        final AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder()
            .setEscEnabled(true)
            .build();

        when(mercadoPagoCheckout.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(configuration.getAdvancedConfiguration()).thenReturn(advancedConfiguration);
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getTokenWithESC();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        provider.paymentRequested = false;

        assertTrue(view.showingPaymentRecoveryFlow);
        PaymentRecovery paymentRecovery = view.paymentRecoveryRequested;
        assertTrue(paymentRecovery.isStatusDetailInvalidESC());
        assertTrue(paymentRecovery.isTokenRecoverable());

        //Response from Card Vault with new Token
        presenter.onCardFlowResponse(issuer, token);
        assertTrue(provider.paymentRequested);

        provider.setPaymentResponse(Payments.getApprovedPayment());
        assertNotNull(provider.paymentResponse);
    }

    @Ignore
    @Test
    public void onCreatePaymentWithESCTokenErrorThenDeleteESC() {
        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        provider.setCheckoutPreferenceResponse(checkoutPreference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));

        ApiException apiException = Payments.getInvalidESCPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");

        AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder()
            .setEscEnabled(true)
            .build();

        when(mercadoPagoCheckout.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(configuration.getAdvancedConfiguration()).thenReturn(advancedConfiguration);
        provider.setPaymentResponse(mpException);

        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        Issuer issuer = Issuers.getIssuers().get(0);
        Token token = Tokens.getTokenWithESC();

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        Cause cause = provider.failedResponse.getApiException().getCause().get(0);
        assertEquals(cause.getCode(), ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void whenChoHasPrefIdSetRetrievePreferenceFromMercadoPago() {
        when(configuration.getCheckoutPreference()).thenReturn(null);
        when(configuration.getCheckoutPreferenceId()).thenReturn("some_pref_id");
        final CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        presenter.initialize();
        verify(checkoutProvider).getCheckoutPreference(any(String.class), any(TaggedCallback.class));
        verifyNoMoreInteractions(checkoutProvider);
    }

    @Test
    public void whenChoHasCompletePrefSetDoNotCallProviderToGetPreference() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        final CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        presenter.initialize();
        verify(checkoutProvider).fetchFonts();
        verify(checkoutProvider, times(0)).getCheckoutPreference(any(String.class), any(TaggedCallback.class));
    }

    @Test
    public void whenPreferenceIsExpiredThenShowErrorInView() {
        final CheckoutPreference preference = stubExpiredPreference();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        presenter.initialize();
        verify(checkoutProvider).getCheckoutExceptionMessage(any(CheckoutPreferenceException.class));
        verify(checkoutView).showError(any(MercadoPagoError.class));
    }

    @Test
    public void whenChoHasPreferenceAndPaymentMethodRetrivedShowPaymentMethodSelection() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        final CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        presenter.initialize();

        verify(groupsRepository).getGroups();
        verify(checkoutView).showProgress();
        verify(checkoutView).initializeMPTracker();
        verify(checkoutView).trackScreen();
        verify(checkoutView).showPaymentMethodSelection();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenAPaymentMethodIsSelectedThenShowReviewAndConfirm() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        final CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        presenter
            .onPaymentMethodSelectionResponse(null, null, null, null);
        verify(checkoutView).showReviewAndConfirm(false);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentMethodCanceledThenCancelCheckout() {
        final CheckoutPresenter presenter = getPresenter(PAYMENT_DATA_RESULT_CODE);
        presenter.onPaymentMethodSelectionCancel();
        verify(checkoutView).cancelCheckout();
    }

    @Test
    public void whenPaymentRequestedAndOnReviewAndConfirmOkResponseThenCreatePayment() {

        final CheckoutPreference preference = stubPreferenceOneItem();

        when(configuration.getCheckoutPreference()).thenReturn(preference);

        final CheckoutPresenter checkoutPresenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);
        //Real preference, without items
        provider.setCheckoutPreferenceResponse(preference);
        provider.setPaymentResponse(Payments.getApprovedPayment());

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuers().get(0);
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        final Token token = Tokens.getVisaToken();

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);

        checkoutPresenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        //Response from Review And confirm
        checkoutPresenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);
    }

    @Test
    public void whenPaymentCreatedThenShowResultScreen() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        //Real preference, without items
        provider.setPaymentResponse(Payments.getApprovedPayment());

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuers().get(0);
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        final Token token = Tokens.getVisaToken();

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);
        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void onPaymentResultScreenResponseThenFinishWithPaymentResponse() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        Payment payment = Payments.getApprovedPayment();
        provider.setPaymentResponse(payment);

        presenter.initialize();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        Token token = Tokens.getVisaToken();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        presenter.onPaymentConfirmation();

        //On Payment Result Screen
        assertEquals(view.paymentFinalResponse, null);

        presenter.onPaymentResultResponse();

        assertEquals(view.paymentFinalResponse.getId(), payment.getId());
    }

    // Forwarded flows
    @Test
    public void whenPaymentDataSetThenStartRyCScreen() {
        CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());
        when(mercadoPagoCheckout.getPaymentData()).thenReturn(paymentData);
        when(configuration.getCheckoutPreference()).thenReturn(preference);

        CheckoutPresenter presenter = getBasePresenter(REQUESTED_RESULT, view, provider);

        provider.setCampaignsResponse(Discounts.getCampaigns());
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));

        presenter.initialize();

        assertFalse(view.showingPaymentMethodSelection);
        assertTrue(view.showingReviewAndConfirm);
    }

    @Test
    public void whenPaymentResultSetThenStartResultScreen() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();

        when(configuration.getCheckoutPreference()).thenReturn(preference);
        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(stubApprovedOffPaymentResult());

        provider.setCampaignsResponse(Discounts.getCampaigns());
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));

        final CheckoutPresenter presenter = getBasePresenter(REQUESTED_RESULT, view, provider);

        presenter.initialize();

        assertFalse(view.showingPaymentMethodSelection);
        assertFalse(view.showingReviewAndConfirm);
        assertTrue(view.showingPaymentResult);
    }

    @Test
    public void whenPaymentResultSetAndUserLeavesScreenThenRespondWithoutPayment() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(stubApprovedOffPaymentResult());

        provider.setCampaignsResponse(Discounts.getCampaigns());
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));

        CheckoutPresenter presenter = getBasePresenter(REQUESTED_RESULT, view, provider);

        presenter.initialize();
        assertTrue(view.showingPaymentResult);

        presenter.onPaymentResultResponse();

        assertTrue(view.finishedCheckoutWithoutPayment);
    }

    @Test
    public void ifPaymentRecoveryRequiredThenStartPaymentRecoveryFlow() {
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        provider.setPaymentResponse(Payments.getCallForAuthPayment());
        final CheckoutPresenter presenter = getPaymentPresenter();
        presenter.initialize();

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuers().get(0);
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        final Token token = Tokens.getVisaToken();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);

        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        assertEquals(view.paymentRecoveryRequested.getPaymentMethod().getId(), paymentMethod.getId());
    }

    @Test
    public void onTokenRecoveryFlowOkResponseThenCreatePayment() {

        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.initialize();

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuers().get(0);
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        final Token token = Tokens.getVisaToken();

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);

        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        assertEquals(view.paymentRecoveryRequested.getPaymentMethod().getId(), paymentMethod.getId());

        presenter.onCardFlowResponse(issuer, token);
        assertTrue(view.showingPaymentResult);

        Assert.assertEquals(paymentMethod.getId(), provider.paymentMethodPaid.getId());
    }

    @Test
    public void ifPaymentRecoveryRequiredWithInvalidPaymentMethodThenShowError() {
        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        provider.setPaymentResponse(Payments.getCallForAuthPayment());

        presenter.initialize();
        final PaymentMethod paymentMethodOff = PaymentMethods
            .getPaymentMethodOff();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOff);

        presenter.onPaymentMethodSelectionResponse(null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingError);
    }

    //Backs
    @Test
    public void ifCheckoutInitiatedAndUserPressesBackCancelCheckout() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.checkoutCanceled);
    }

    //
    @Ignore
    @Test
    public void ifReviewAndConfirmShownAndUserPressesBackThenRestartPaymentMethodSelection() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        final PaymentMethod paymentMethodOff = PaymentMethods.getPaymentMethodOff();
        presenter
            .onPaymentMethodSelectionResponse(null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onReviewAndConfirmCancel();
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void ifPaymentRecoveryShownAndUserPressesBackThenRestartPaymentMethodSelection() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        provider.setPaymentResponse(Payments.getCallForAuthPayment());
        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);
        final PaymentMethod paymentMethodOnVisa = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOnVisa);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);
        presenter.onPaymentMethodSelectionResponse(
            Issuers.getIssuers().get(0), Tokens.getVisaToken(), null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();
        assertTrue(view.showingPaymentResult);
        presenter.onPaymentResultCancel(PaymentResult.RECOVER_PAYMENT);
        assertTrue(view.showingPaymentRecoveryFlow);
        presenter.onCardFlowCancel();
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void ifPaymentMethodEditionRequestedAndUserPressesBackTwiceCancelCheckout() {
        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();

        presenter.initialize();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionResponse(null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);

        presenter.onChangePaymentMethodFromReviewAndConfirm();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.showingReviewAndConfirm);

        presenter.onReviewAndConfirmCancel();
        assertTrue(view.showingPaymentMethodSelection);

        presenter.onPaymentMethodSelectionCancel();
        assertTrue(view.checkoutCanceled);
    }

    //Payment tests
    @Test
    public void whenPaymentCreationRequestedThenGenerateTransactionId() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        provider.setPaymentResponse(Payments.getApprovedPayment());

        presenter.initialize();
        final PaymentMethod paymentMethodOff = PaymentMethods.getPaymentMethodOff();
        //Payment method off, no issuer, installments or token
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOff);

        presenter
            .onPaymentMethodSelectionResponse(null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();

        assertTrue(provider.paymentRequested);
        assertFalse(TextUtils.isEmpty(provider.transactionId));
    }

    // TODO CHECK IF WE WILL SUPPORT THIS KIND OF PM requests.
    @Ignore
    @Test
    public void whenCustomerAvailableAndPaymentCreationRequestedThenCreatePaymentWithCustomerId() {
        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        provider.setPaymentResponse(Payments.getApprovedPayment());
        provider.setCustomerResponse(Customers.getCustomerWithCards());
        presenter.initialize();

        //Payment method off, no issuer, installments or token
        final PaymentMethod paymentMethodOff = PaymentMethods.getPaymentMethodOff();

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOff);
        presenter
            .onPaymentMethodSelectionResponse(null, null, null, null);
        assertTrue(view.showingReviewAndConfirm);
        presenter.onPaymentConfirmation();

        assertTrue(provider.paymentRequested);
        assertFalse(TextUtils.isEmpty(provider.paymentCustomerId));
    }

    @Test
    public void ifPaymentResultApprovedSetAndTokenWithESCAndESCEnabledThenSaveESC() {
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with card Id and ESC
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        final CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(1234L)
            .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_ACCREDITED)
            .build();

        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);

        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(paymentResult);
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void ifPaymentResultApprovedSetAndESCEnabledButTokenHasNoESCThenDontSaveESC() {

        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token without card id or ESC
        Token token = Tokens.getVisaToken();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        final PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(1234L)
            .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_ACCREDITED)
            .build();

        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(paymentResult);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        final CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void ifPaymentResultApprovedSetAndESCEnabledThenShowPaymentResultScreen() {

        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with card Id and ESC
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(1234L)
            .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_ACCREDITED)
            .build();


        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(paymentResult);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);
        presenter.initialize();

        assertTrue(view.showingPaymentResult);
    }

    //Ignored case - the it had sense when review and confirm could be canceled, not anymore.
    @Ignore
    @Test
    public void createPaymentWithESCTokenThenSaveESC() {

        final CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        provider.setPaymentResponse(Payments.getApprovedPayment());

        final AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder()
            .setEscEnabled(true)
            .build();

        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        when(configuration.getAdvancedConfiguration()).thenReturn(advancedConfiguration);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        final CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final Issuer issuer = Issuers.getIssuers().get(0);
        final Token token = Tokens.getTokenWithESC();

        final Card mockedCard = Cards.getCard();
        mockedCard.setId("12345");
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, mockedCard, null);

        //TODO do payment

        //Response from Review And confirm
        assertTrue(provider.paymentRequested);
        assertNotNull(provider.paymentResponse);
        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void ifPaymentResultInvalidESCSetAndESCEnabledThenDontSaveESC() {
        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();

        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token without card id or ESC
        Token token = Tokens.getVisaToken();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(1234L)
            .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC)
            .build();

        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(paymentResult);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void ifPaymentResultInvalidESCSetAndESCEnabledThenDeleteESCSaved() {

        CheckoutPreference checkoutPreference = stubPreferenceWithAccessToken();
        PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        Issuer issuer = Issuers.getIssuers().get(0);
        PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        //Token with Card ID (because it was created with ESC enabled)
        Token token = Tokens.getTokenWithESC();

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setIssuer(issuer);
        paymentData.setPayerCost(payerCost);
        paymentData.setToken(token);

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentData(paymentData)
            .setPaymentId(1234L)
            .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC)
            .build();

        when(mercadoPagoCheckout.getPaymentResult()).thenReturn(paymentResult);
        when(configuration.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        assertTrue(provider.manageEscRequested);
    }

    @Test
    public void ifPayerDataCollectedAndPayerInPreferenceThenUseBothForPayment() {
        String firstName = "FirstName";
        String lastName = "LastName";
        Identification identification = new Identification();
        identification.setType("cpf");
        identification.setNumber("111");

        Payer collectedPayer = new Payer();
        collectedPayer.setFirstName(firstName);
        collectedPayer.setLastName(lastName);
        collectedPayer.setIdentification(identification);

        provider.setPaymentResponse(Payments.getCallForAuthPayment());
        CheckoutPreference preference = stubPreferenceOneItem();

        when(configuration.getCheckoutPreference()).thenReturn(preference);
        provider.setCheckoutPreferenceResponse(preference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        final CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);

        presenter.initialize();

        final PaymentMethod paymentMethodOff = PaymentMethods.getPaymentMethodOff();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOff);
        presenter.onPaymentMethodSelectionResponse(null, null, null, collectedPayer);
        presenter.onPaymentConfirmation();

        assertEquals(provider.payerPosted.getEmail(), preference.getPayer().getEmail());
        assertEquals(provider.payerPosted.getAccessToken(), preference.getPayer().getAccessToken());
        assertEquals(provider.payerPosted.getFirstName(), firstName);
        assertEquals(provider.payerPosted.getLastName(), lastName);
        assertEquals(provider.payerPosted.getIdentification().getType(), identification.getType());
        assertEquals(provider.payerPosted.getIdentification().getNumber(), identification.getNumber());
    }

    @Test
    public void ifOnlyPayerFromPreferenceThenUseItForPayment() {
        CheckoutPreference preference = stubPreferenceWithAccessToken();
        when(configuration.getCheckoutPreference()).thenReturn(preference);
        provider.setCheckoutPreferenceResponse(preference);
        when(groupsRepository.getGroups())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getCompletePaymentMethodSearchMLA()));
        provider.setPaymentResponse(Payments.getCallForAuthPayment());
        CheckoutPresenter presenter = getBasePresenter(PAYMENT_RESULT_CODE, view, provider);
        presenter.initialize();

        final PaymentMethod paymentMethodOff = PaymentMethods.getPaymentMethodOff();
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethodOff);
        presenter
            .onPaymentMethodSelectionResponse(null, null, null, null);
        presenter.onPaymentConfirmation();
        assertEquals(provider.payerPosted.getEmail(), preference.getPayer().getEmail());
        assertEquals(provider.payerPosted.getAccessToken(), preference.getPayer().getAccessToken());
    }

    @Test
    public void onIdentificationInvalidAndErrorShownThenGoBackToPaymentMethodSelection() {
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        ApiException apiException = Payments.getInvalidIdentificationPayment();
        MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        presenter.initialize();

        presenter.onErrorCancel(mpException);
        assertTrue(view.showingPaymentMethodSelection);
    }

    @Test
    public void createPaymentWithInvalidIdentificationThenShowError() {
        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();

        final ApiException apiException = Payments.getInvalidIdentificationPayment();
        final MercadoPagoError mpException = new MercadoPagoError(apiException, "");
        provider.setPaymentResponse(mpException);

        presenter.initialize();

        final PaymentMethod paymentMethod = PaymentMethods.getPaymentMethodOnVisa();
        final PayerCost payerCost = Installments.getInstallments().getPayerCosts().get(0);
        final Issuer issuer = Issuers.getIssuers().get(0);
        final Token token = Tokens.getTokenWithESC();

        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(userSelectionRepository.getPayerCost()).thenReturn(payerCost);

        //Response from payment method selection
        presenter.onPaymentMethodSelectionResponse(issuer, token, null, null);

        //Response from Review And confirm
        presenter.onPaymentConfirmation();
        assertTrue(provider.paymentRequested);

        final Cause cause = provider.failedResponse.getApiException().getCause().get(0);
        assertEquals(cause.getCode(), ApiException.ErrorCodes.INVALID_IDENTIFICATION_NUMBER);
        assertTrue(view.showingError);
    }

    @Test
    public void ifNotNewFlowThenDoNotTrackInit() {
        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());
        when(mercadoPagoCheckout.getPaymentData()).thenReturn(paymentData);
        CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        presenter.initialize();
        assertFalse(view.initTracked);
    }

    @Test
    public void ifNewFlowThenDoTrackInit() {
        final CheckoutPresenter presenter = getPaymentPresenterWithDefaultAdvancedConfigurationMla();
        presenter.initialize();
        assertTrue(view.initTracked);
    }

    private static class MockedView implements CheckoutView {

        MercadoPagoError errorShown;
        boolean showingError = false;
        boolean showingPaymentMethodSelection = false;
        boolean showingReviewAndConfirm = false;
        boolean initTracked = false;
        PaymentData paymentDataFinalResponse;
        boolean showingPaymentResult = false;
        boolean checkoutCanceled = false;
        Payment paymentFinalResponse;
        boolean finishedCheckoutWithoutPayment = false;
        boolean showingPaymentRecoveryFlow = false;
        PaymentRecovery paymentRecoveryRequested;

        @Override
        public void fetchImageFromUrl(String url) {
            //Do nothing
        }

        @Override
        public void showBusinessResult(final BusinessPaymentModel model) {
            //Do nothing
        }

        @Override
        public void showOneTap(@NonNull final OneTapModel oneTapModel) {
            //Do nothing
        }

        @Override
        public void hideProgress() {
            //Do nothing
        }

        @Override
        public void exitCheckout(final int resCode) {
            //Do nothing
        }

        @Override
        public void transitionOut() {
            //Do nothing
        }

        @Override
        public void showError(MercadoPagoError error) {
            this.showingError = true;
            this.errorShown = error;
        }

        @Override
        public void showProgress() {
            //Do nothing
        }

        @Override
        public void showReviewAndConfirm(final boolean isUniquePaymentMethod) {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = true;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void showPaymentMethodSelection() {
            showingPaymentMethodSelection = true;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void showPaymentResult(PaymentResult paymentResult) {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = false;
            showingPaymentResult = true;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void backToReviewAndConfirm() {
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = true;
            showingPaymentResult = false;
            showingPaymentRecoveryFlow = false;
        }

        @Override
        public void finishWithPaymentResult() {
            finishedCheckoutWithoutPayment = true;
        }

        @Override
        public void finishWithPaymentResult(Integer customResultCode) {

        }

        @Override
        public void finishWithPaymentResult(Payment payment) {
            paymentFinalResponse = payment;
        }

        @Override
        public void finishWithPaymentResult(Integer customResultCode, Payment payment) {

        }

        @Override
        public void finishWithPaymentDataResult(PaymentData paymentData, Boolean paymentMethodEdited) {
            paymentDataFinalResponse = paymentData;
        }

        @Override
        public void cancelCheckout() {
            checkoutCanceled = true;
        }

        @Override
        public void cancelCheckout(MercadoPagoError mercadoPagoError) {

        }

        @Override
        public void cancelCheckout(final Integer customResultCode, final Boolean paymentMethodEdited) {

        }

        @Override
        public void startPaymentRecoveryFlow(PaymentRecovery paymentRecovery) {
            paymentRecoveryRequested = paymentRecovery;
            showingPaymentRecoveryFlow = true;
            showingPaymentMethodSelection = false;
            showingReviewAndConfirm = false;
            showingPaymentResult = false;
        }

        @Override
        public void initializeMPTracker() {

        }

        @Override
        public void trackScreen() {
            initTracked = true;
        }

        @Override
        public void showHook(Hook hook, int requestCode) {

        }

        @Override
        public void showPaymentProcessor() {

        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    public class MockedProvider implements CheckoutProvider {

        private boolean campaignsRequested = false;

        private List<Campaign> campaigns;
        private boolean checkoutPreferenceRequested = false;
        private CheckoutPreference preference;
        private boolean paymentMethodSearchRequested = false;
        private PaymentMethodSearch paymentMethodSearchResponse;
        private Payment paymentResponse;
        private boolean paymentRequested;
        private Customer customerResponse;

        private String transactionId;
        private String paymentCustomerId;
        private PaymentMethod paymentMethodPaid;
        private Payer payerPosted;

        private boolean shouldFail = false;
        private MercadoPagoError failedResponse;
        public boolean manageEscRequested = false;

        @Override
        public void fetchFonts() {

        }

        @Override
        public boolean manageEscForPayment(final PaymentData paymentData, final String paymentStatus,
            final String paymentStatusDetail) {
            manageEscRequested = true;
            return false;
        }

        @Override
        public boolean manageEscForError(final MercadoPagoError error, final PaymentData paymentData) {
            return false;
        }

        @Override
        public void getCheckoutPreference(String checkoutPreferenceId,
            TaggedCallback<CheckoutPreference> taggedCallback) {
            checkoutPreferenceRequested = true;
            taggedCallback.onSuccess(preference);
        }

        @Override
        public String getCheckoutExceptionMessage(CheckoutPreferenceException exception) {
            return null;
        }

        @Override
        public String getCheckoutExceptionMessage(IllegalStateException exception) {
            return null;
        }

        @Override
        public void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData,
            Boolean binaryMode, String customerId, TaggedCallback<Payment> taggedCallback) {
            this.paymentMethodPaid = paymentData.getPaymentMethod();
            this.transactionId = transactionId;
            this.paymentCustomerId = customerId;
            this.paymentRequested = true;
            this.payerPosted = paymentData.getPayer();
            if (shouldFail) {
                taggedCallback.onFailure(failedResponse);
            } else {
                taggedCallback.onSuccess(paymentResponse);
            }
        }

        public void setCampaignsResponse(List<Campaign> campaigns) {
            this.campaigns = campaigns;
        }

        public void setCheckoutPreferenceResponse(CheckoutPreference preference) {
            this.preference = preference;
        }

        public void setPaymentResponse(Payment paymentResponse) {
            this.paymentResponse = paymentResponse;
        }

        public void setCustomerResponse(Customer customerResponse) {
            this.customerResponse = customerResponse;
        }

        public void setPaymentResponse(MercadoPagoError error) {
            shouldFail = true;
            failedResponse = error;
        }
    }
}
