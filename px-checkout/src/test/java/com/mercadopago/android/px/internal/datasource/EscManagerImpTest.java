package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.internal.util.ApiUtil.StatusCodes.BAD_REQUEST;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EscManagerImpTest {

    @Mock private MercadoPagoESC mercadoPagoESC;

    private EscManagerImp escManager;

    @Before
    public void setUp() {
        escManager = new EscManagerImp(mercadoPagoESC);
    }

    @NonNull
    private PaymentData validCardPaymentData() {
        final Token token = mock(Token.class);
        when(token.getEsc()).thenReturn("fake esc");
        when(token.getCardId()).thenReturn("fake card id");
        final PaymentData paymentData = mock(PaymentData.class);
        when(paymentData.containsCardInfo()).thenReturn(true);
        when(paymentData.getToken()).thenReturn(token);
        return paymentData;
    }

    @NonNull
    private MercadoPagoError escMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Collections.singletonList(cause));
        return error;
    }

    @NonNull
    private MercadoPagoError noEscMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Collections.singletonList(cause));
        return error;
    }

    @NonNull
    private MercadoPagoError multipleErrorEscMpError() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        final ApiException apiException = mock(ApiException.class);
        final Cause cause = mock(Cause.class);
        when(cause.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH);
        final Cause cause2 = mock(Cause.class);
        when(cause2.getCode()).thenReturn(ApiException.ErrorCodes.INVALID_PAYMENT_WITH_ESC);
        when(error.isApiException()).thenReturn(true);
        when(error.getApiException()).thenReturn(apiException);
        when(apiException.getStatus()).thenReturn(BAD_REQUEST);
        when(apiException.getCause()).thenReturn(Arrays.asList(cause, cause2));
        return error;
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsApproveSavesCardReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid = escManager.manageEscForPayment(paymentData, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
        verify(mercadoPagoESC).saveESC(paymentData.getToken().getCardId(), paymentData.getToken().getEsc());
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsRejectedDoNothingReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid = escManager.manageEscForPayment(paymentData, Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE);
        verify(mercadoPagoESC).deleteESC(paymentData.getToken().getCardId());
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasValidPaymentDataAndIsRejectedEscInvalidDeleteESCReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final boolean invalid = escManager.manageEscForPayment(paymentData, Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verify(mercadoPagoESC).deleteESC(paymentData.getToken().getCardId());
        verifyNoMoreInteractions(mercadoPagoESC);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasNonCardPaymentDataAndIsRejectedDoNothingReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final boolean invalid = escManager.manageEscForPayment(paymentData, Payment.StatusCodes.STATUS_REJECTED,
            Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForPaymentHasNonCardPaymentDataAndIsApprovedDoNothingReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final boolean invalid = escManager.manageEscForPayment(paymentData, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithCauseInvalidEscDeleteEscAndReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = escMpError();
        final boolean invalid = escManager.manageEscForError(error, paymentData);
        verify(mercadoPagoESC).deleteESC(paymentData.getToken().getCardId());
        verifyNoMoreInteractions(mercadoPagoESC);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithMultipleCauseAndInvalidEscDeleteEscAndReturnTrue() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = multipleErrorEscMpError();
        final boolean invalid = escManager.manageEscForError(error, paymentData);
        verify(mercadoPagoESC).deleteESC(paymentData.getToken().getCardId());
        verifyNoMoreInteractions(mercadoPagoESC);
        assertTrue(invalid);
    }

    @Test
    public void whenManageEscForErrorHasCardPaymentDataBadRequestWithNoEscCauseReturnFalse() {
        final PaymentData paymentData = validCardPaymentData();
        final MercadoPagoError error = noEscMpError();
        final boolean invalid = escManager.manageEscForError(error, paymentData);
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }

    @Test
    public void whenManageEscForErrorNoCardPaymentDataBadRequestWithNoEscCauseReturnFalse() {
        final PaymentData paymentData = mock(PaymentData.class);
        final MercadoPagoError error = noEscMpError();
        final boolean invalid = escManager.manageEscForError(error, paymentData);
        verifyNoMoreInteractions(mercadoPagoESC);
        assertFalse(invalid);
    }
}