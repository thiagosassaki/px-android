package com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method;

import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.PaymentModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodComponentTest {

    @Mock
    private
    PaymentModel model;

    private PaymentMethodComponent component;

    @Before
    public void setUp() {
        component = new PaymentMethodComponent(model, null);
    }

    @Test
    public void whenPaymentTypeIsCardResolveComponentReturnsAMethodCardInstance() {
        when(model.getPaymentType()).thenReturn(PaymentTypes.CREDIT_CARD);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodCard);
    }

    @Test
    public void whenPaymentTypeIsOffThenResolveComponentReturnsAMethodOffInstance() {
        when(model.getPaymentType()).thenReturn(PaymentTypes.BANK_TRANSFER);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodOff);
    }

    @Test
    public void whenPaymentTypeIsAccountMoneyThenResolveComponentReturnsAMethodPluginInstance() {
        when(model.getPaymentType()).thenReturn(PaymentTypes.ACCOUNT_MONEY);
        CompactComponent instance = component.resolveComponent();
        assertTrue(instance instanceof MethodPlugin);
    }

    @Test
    public void whenPaymentMethodIsOnlyAvailableAndIsNotCreditCardShouldNotShowPaymentMethodButton() {
        when(model.getPaymentType()).thenReturn(PaymentTypes.BANK_TRANSFER);
        when(model.hasMoreThanOnePaymentMethod()).thenReturn(false);
        assertFalse(component.shouldShowPaymentMethodButton());
    }

    @Test
    public void whenPaymentMethodIsOnlyAvailableAndIsCreditCardShouldShowPaymentMethodButton() {
        when(model.getPaymentType()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(model.hasMoreThanOnePaymentMethod()).thenReturn(false);
        assertTrue(component.shouldShowPaymentMethodButton());
    }

    @Test
    public void whenPaymentMethodIsNotOnlyAvailableShowPaymentMethodButton() {
        when(model.hasMoreThanOnePaymentMethod()).thenReturn(true);
        assertTrue(component.shouldShowPaymentMethodButton());
    }
}