package com.mercadopago.android.px.internal.driver;

import com.mercadopago.android.px.internal.navigation.DefaultPaymentMethodDriver;
import com.mercadopago.android.px.mocks.PaymentMethodSearchs;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.preferences.PaymentPreference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentMethodDriverTest {

    private static final String STUB_CARD_VISA = "visa";
    private static final String STUB_CREDIT_CARD = "credit_card";
    private static final String STUB_CARD_ID_PM_STUB_SOURCE = "122232111";

    @Mock private DefaultPaymentMethodDriver.PaymentMethodDriverCallback paymentMethodDriverCallback;
    @Mock private PaymentPreference paymentPreference;
    private PaymentMethodSearch paymentMethods;
    private DefaultPaymentMethodDriver handler;

    @Before
    public void setUp() {
        paymentMethods = PaymentMethodSearchs.getPaymentMethodSearchWithSavedCardsMLA();
        handler = new DefaultPaymentMethodDriver(paymentMethods, paymentPreference);
    }

    @Test
    public void whenPaymentPreferenceIsNullThenDoNothing() {
        new DefaultPaymentMethodDriver(paymentMethods, null).drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).doNothing();
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }

    @Test
    public void whenPaymentMethodIsCardAndCardIdIsNullThenDriveToNewCardFlow(){
        when(paymentPreference.getDefaultPaymentTypeId()).thenReturn(STUB_CREDIT_CARD);
        handler.drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).driveToNewCardFlow();
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }

    @Test
    public void whenPaymentMethodIsCardAndCardIdIsNullThenAutomaticSelectionDriveToNewCardFlow() {
        when(paymentPreference.getDefaultPaymentTypeId()).thenReturn(STUB_CREDIT_CARD);
        when(paymentPreference.getDefaultCardId()).thenReturn(null);
        handler.drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).driveToNewCardFlow();
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }

    @Test
    public void whenPaymentMethodIsNotCardAndAnyCardIdThenAutomaticSelectionDriveToPaymentVault() {
        when(paymentPreference.getDefaultPaymentMethodId()).thenReturn(PaymentTypes.TICKET);
        handler.drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).doNothing();
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }

    @Test
    public void whenPaymentMethodIsCardAndCardIdIsNotNullButIsNotValidThenAutomaticSelectionDriveToPaymentVault() {
        when(paymentPreference.getDefaultPaymentMethodId()).thenReturn(STUB_CARD_VISA);
        when(paymentPreference.getDefaultCardId()).thenReturn("4321");
        handler.drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).doNothing();
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }

    @Test
    public void whenPaymentMethodIsCardAndCardIdIsNotNullThenAutomaticSelectionDriveToCardVault() {
        when(paymentPreference.getDefaultPaymentMethodId()).thenReturn(STUB_CARD_VISA);
        when(paymentPreference.getDefaultCardId()).thenReturn(STUB_CARD_ID_PM_STUB_SOURCE);
        final Card card = paymentMethods.getCardById(STUB_CARD_ID_PM_STUB_SOURCE);
        handler.drive(paymentMethodDriverCallback);
        verify(paymentMethodDriverCallback).driveToCardVault(card);
        verifyNoMoreInteractions(paymentMethodDriverCallback);
    }
}
