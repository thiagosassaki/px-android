package com.mercadopago.android.px.internal.view;

import android.content.Context;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.ReceiptRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptRendererTest {

    @Mock
    private Context context;

    private ReceiptRenderer receiptRenderer;

    private static final String MOCK_RECEIPT_PLACEHOLDER = "M OCK %s";

    @Before
    public void setUp() {
        receiptRenderer = new ReceiptRenderer();
    }

    @Test
    public void whenReceiptIdIsNullThenDescriptionIsEmptyString() {
        String receiptDescription = receiptRenderer.getReceiptDescription(context, null);
        assertEquals("", receiptDescription);
    }

    @Test
    public void whenReceiptIdIsNotNuThenDescriptionIsEmptyString() {
        String longReceipt = String.valueOf(120L);
        String expected = String.format(MOCK_RECEIPT_PLACEHOLDER, String.valueOf(longReceipt));
        when(context.getString(R.string.px_receipt, String.valueOf(longReceipt))).thenReturn(expected);
        String receiptDescription = receiptRenderer.getReceiptDescription(context, longReceipt);
        assertEquals(expected, receiptDescription);
    }
}