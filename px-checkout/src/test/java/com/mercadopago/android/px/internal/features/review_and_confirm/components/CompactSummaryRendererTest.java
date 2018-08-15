package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompactSummaryRendererTest {

    @Mock
    private SummaryModel model;

    private CompactSummaryRenderer compactSummaryRenderer;

    @Before
    public void setUp() {
        compactSummaryRenderer = new CompactSummaryRenderer();
    }

    @Test
    public void whenInstallmentsMoreThanOneAndPaymentTypeCreditCardShowCFT() {
        when(model.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(model.getCftPercent()).thenReturn("123");
        assertTrue(compactSummaryRenderer.shouldShowCftDisclaimer(model));
    }

    @Test
    public void whenInstallmentsMoreThanOneAndPaymentTypeDebitCardDoNotShowCFT() {
        when(model.getPaymentTypeId()).thenReturn(PaymentTypes.DEBIT_CARD);
        assertFalse(compactSummaryRenderer.shouldShowCftDisclaimer(model));
    }

    @Test
    public void whenInstallmentsMoreThanOneAndPaymentTypeCreditCardButNoCftDoNotShowCFT() {
        when(model.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(model.getCftPercent()).thenReturn("");
        assertFalse(compactSummaryRenderer.shouldShowCftDisclaimer(model));
    }

    @Test
    public void whenInstallmentsOneAndPaymentTypeCreditCardDoNotShowCFT() {
        when(model.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        assertFalse(compactSummaryRenderer.shouldShowCftDisclaimer(model));
    }
}