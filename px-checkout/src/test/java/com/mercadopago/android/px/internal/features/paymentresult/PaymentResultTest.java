package com.mercadopago.android.px.internal.features.paymentresult;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.mocks.PaymentMethods;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.math.BigDecimal;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentResultTest {

    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private CheckoutPreference checkoutPreference;
    @Before
    public void setUp(){
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(checkoutPreference.getSite()).thenReturn(Sites.ARGENTINA);
    }
    
    @Test
    public void whenPaymentWithCardApprovedThenShowCongrats() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_APPROVED)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        //TODO fix
//        Assert.assertTrue(mockedView.congratsShown);
    }

    @Test
    public void whenPaymentWithCardRejectedThenShowRejection() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        //TODO fix
//        Assert.assertTrue(mockedView.rejectionShown);
    }

    @Test
    public void whenCallForAuthNeededThenShowCallForAuthScreen() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        //TODO fix
//        Assert.assertTrue(mockedView.callForAuthorizeShown);
    }

    @Test
    public void whenPaymentOffPendingThenShowInstructions() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_PENDING)
            .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        //TODO fix
//        Assert.assertTrue(mockedView.instructionsShown);
    }

    @Test
    public void whenPaymentOnInProcessThenShowPendingScreen() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_IN_PROCESS)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        //TODO fix
//        Assert.assertTrue(mockedView.pendingShown);
    }

    @Test
    public void whenPaymentOffRejectedThenShowRejection() {

        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOff());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(Payment.StatusCodes.STATUS_REJECTED)
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();
        //TODO fix
//        Assert.assertTrue(mockedView.rejectionShown);
    }

    @Test
    public void whenUnknownStatusThenShowError() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus("UNKNOWN")
            .setPaymentData(paymentData)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(navigator.errorShown);
    }

    @Test
    public void whenPaymentDataIsNullThenShowError() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus("UNKNOWN")
            .setPaymentData(null)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(navigator.errorShown);
    }

    @Test
    public void whenPaymentResultIsNullThenShowError() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        presenter.setPaymentResult(null);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(navigator.errorShown);
    }

    @Test
    public void whenPaymentResultStatusIsNullThenShowError() {
        MockedNavigator navigator = new MockedNavigator();
        PaymentResultPresenter presenter = new PaymentResultPresenter(navigator,
            paymentSettingRepository);

        PaymentData paymentData = new PaymentData();
        paymentData.setPaymentMethod(PaymentMethods.getPaymentMethodOnVisa());

        PaymentResult paymentResult = new PaymentResult.Builder()
            .setPaymentStatus(null)
            .setPaymentData(null)
            .build();

        presenter.setPaymentResult(paymentResult);
        presenter.setAmount(new BigDecimal("100"));
        ;

        MockedPropsView mockedView = new MockedPropsView();
        MockedProvider mockedProvider = new MockedProvider();

        presenter.attachView(mockedView);
        presenter.attachResourcesProvider(mockedProvider);

        presenter.initialize();

        Assert.assertTrue(navigator.errorShown);
    }

    private class MockedPropsView implements PaymentResultPropsView {

        @Override
        public void setPropPaymentResult(@NonNull final String currencyId,
            @NonNull final PaymentResult paymentResult,
            final boolean showLoading) {
            //Do nothing
        }

        @Override
        public void setPropInstruction(@NonNull final Instruction instruction,
            @NonNull final String processingModeString,
            final boolean showLoading) {
            //Do nothing
        }

        @Override
        public void notifyPropsChanged() {
            //Do nothing
        }
    }

    private class MockedProvider implements PaymentResultProvider {

        private String STANDARD_ERROR_MESSAGE = "Algo salió mal";

        @Override
        public void getInstructionsAsync(Long paymentId, String paymentTypeId,
            TaggedCallback<Instructions> taggedCallback) {

        }

        @Override
        public String getStandardErrorMessage() {
            return STANDARD_ERROR_MESSAGE;
        }

        @Override
        public String getApprovedTitle() {
            return null;
        }

        @Override
        public String getPendingTitle() {
            return null;
        }

        @Override
        public String getRejectedOtherReasonTitle(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedInsufficientAmountTitle(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedDuplicatedPaymentTitle(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedCardDisabledTitle(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedBadFilledCardTitle(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedBadFilledCardTitle() {
            return null;
        }

        @Override
        public String getRejectedHighRiskTitle() {
            return null;
        }

        @Override
        public String getRejectedMaxAttemptsTitle() {
            return null;
        }

        @Override
        public String getRejectedInsufficientDataTitle() {
            return null;
        }

        @Override
        public String getRejectedCallForAuthorizeTitle() {
            return null;
        }

        @Override
        public String getRejectedBadFilledOther() {
            return null;
        }

        @Override
        public String getEmptyText() {
            return null;
        }

        @Override
        public String getPendingLabel() {
            return null;
        }

        @Override
        public String getRejectionLabel() {
            return null;
        }

        @Override
        public String getCancelPayment() {
            return null;
        }

        @Override
        public String getContinueShopping() {
            return null;
        }

        @Override
        public String getExitButtonDefaultText() {
            return null;
        }

        @Override
        public String getChangePaymentMethodLabel() {
            return null;
        }

        @Override
        public String getRecoverPayment() {
            return null;
        }

        @Override
        public String getCardEnabled() {
            return null;
        }

        @Override
        public String getErrorTitle() {
            return null;
        }

        @Override
        public String getPendingContingencyBodyErrorDescription() {
            return null;
        }

        @Override
        public String getPendingReviewManualBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedCallForAuthBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedCardDisabledBodyErrorDescription(String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedInsufficientAmountBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedInsufficientAmountBodyErrorSecondDescription() {
            return null;
        }

        @Override
        public String getRejectedOtherReasonBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedByBankBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedInsufficientDataBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedDuplicatedPaymentBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedMaxAttemptsBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedHighRiskBodyErrorDescription() {
            return null;
        }

        @Override
        public String getRejectedCallForAuthBodyActionText(final String paymentMethodName) {
            return null;
        }

        @Override
        public String getRejectedCallForAuthBodySecondaryTitle() {
            return null;
        }

        @Override
        public String getReceiptDescription(final Long receiptId) {
            return null;
        }
    }

    private class MockedNavigator implements PaymentResultNavigator {

        private boolean errorShown = false;

        @Override
        public void showApiExceptionError(ApiException exception, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void showError(MercadoPagoError error, String requestOrigin) {
            this.errorShown = true;
        }

        @Override
        public void openLink(String url) {

        }

        @Override
        public void changePaymentMethod() {

        }

        @Override
        public void finishWithResult(int resultCode) {

        }

        @Override
        public void recoverPayment() {

        }

        @Override
        public void trackScreen(ScreenViewEvent event) {

        }
    }
}