package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.internal.repository.EscManager;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceHandlerWrapperTest {

    @Mock private PaymentServiceHandler wrapped;
    @Mock private PaymentRepository paymentRepository;
    @Mock private EscManager escManager;

    private PaymentServiceHandlerWrapper paymentServiceHandlerWrapper;

    @Before
    public void setUp() {
        paymentServiceHandlerWrapper =
            new PaymentServiceHandlerWrapper(wrapped, paymentRepository, escManager);
        when(paymentRepository.getPaymentData()).thenReturn(mock(PaymentData.class));
    }

    private void noMoreInteractions() {
        verifyNoMoreInteractions(wrapped);
        verifyNoMoreInteractions(escManager);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void whenVisualRequired() {
        paymentServiceHandlerWrapper.onVisualPayment();
        verify(wrapped).onVisualPayment();
        noMoreInteractions();
    }

    @Test
    public void whenOnCvvRequiredVerifyOnCvvRequired() {
        final Card mock = mock(Card.class);
        paymentServiceHandlerWrapper.onCvvRequired(mock);
        verify(wrapped).onCvvRequired(mock);
        noMoreInteractions();
    }

    @Test
    public void whenRecoverPaymentEscInvalidVerifyRecoverPaymentEscInvalid() {
        paymentServiceHandlerWrapper.onRecoverPaymentEscInvalid();
        verify(wrapped).onRecoverPaymentEscInvalid();
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithPaymentVerifyEscManaged() {
        final Payment payment = mock(Payment.class);
        paymentServiceHandlerWrapper.onPaymentFinished(payment);

        verify(escManager).manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());

        verify(paymentRepository, times(2)).getPaymentData();
        verify(wrapped).onPaymentFinished(payment);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithBusinessVerifyEscManaged() {
        final BusinessPayment payment = mock(BusinessPayment.class);
        paymentServiceHandlerWrapper.onPaymentFinished(payment);

        verify(escManager).manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());

        verify(paymentRepository, times(2)).getPaymentData();
        verify(wrapped).onPaymentFinished(payment);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithGenericPaymentVerifyEscManaged() {
        final GenericPayment payment = mock(GenericPayment.class);
        paymentServiceHandlerWrapper.onPaymentFinished(payment);

        verify(escManager).manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());

        verify(paymentRepository, times(2)).getPaymentData();
        verify(wrapped).onPaymentFinished(payment);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithErrorVerifyEscManaged() {
        final MercadoPagoError error = mock(MercadoPagoError.class);
        paymentServiceHandlerWrapper.onPaymentError(error);

        verify(escManager).manageEscForError(error, paymentRepository.getPaymentData());

        verify(paymentRepository, times(2)).getPaymentData();
        verify(wrapped).onPaymentError(error);
        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithGenericPaymentAndEscIsInvalidatedVerifyRecoveryCalled() {
        final GenericPayment payment = mock(GenericPayment.class);

        when(escManager.manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail())).thenReturn(true);

        paymentServiceHandlerWrapper.onPaymentFinished(payment);

        verify(escManager).manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());

        verify(paymentRepository, times(3)).getPaymentData();
        verify(wrapped).onRecoverPaymentEscInvalid();

        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithPaymentAndEscIsInvalidatedVerifyRecoveryCalled() {
        final Payment payment = mock(Payment.class);

        when(escManager.manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail())).thenReturn(true);

        paymentServiceHandlerWrapper.onPaymentFinished(payment);

        verify(escManager).manageEscForPayment(paymentRepository.getPaymentData(), payment.getPaymentStatus(),
            payment.getPaymentStatusDetail());

        verify(paymentRepository, times(3)).getPaymentData();
        verify(wrapped).onRecoverPaymentEscInvalid();

        noMoreInteractions();
    }

    @Test
    public void whenPaymentFinishedWithErrorAndEscIsInvalidatedVerifyRecoveryCalled() {
        final MercadoPagoError error = mock(MercadoPagoError.class);

        when(escManager.manageEscForError(error, paymentRepository.getPaymentData())).thenReturn(true);

        paymentServiceHandlerWrapper.onPaymentError(error);

        verify(escManager).manageEscForError(error, paymentRepository.getPaymentData());

        verify(paymentRepository, times(3)).getPaymentData();
        verify(wrapped).onRecoverPaymentEscInvalid();

        noMoreInteractions();
    }
}