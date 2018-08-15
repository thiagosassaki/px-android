package com.mercadopago.android.px.internal.features.paymentresult;

import com.mercadopago.android.px.internal.view.Receipt;
import com.mercadopago.android.px.mocks.PaymentResults;
import com.mercadopago.android.px.model.PaymentResult;
import junit.framework.Assert;
import org.junit.Test;

public class ReceiptTest {

    @Test
    public void testReceiptPropsAreValid() {
        final PaymentResult paymentResult = PaymentResults.getStatusApprovedPaymentResult();
        String stringPaymentId = String.valueOf(paymentResult.getPaymentId());
        final Receipt.ReceiptProps receiptProps = new Receipt.ReceiptProps(stringPaymentId);
        final Receipt receipt = new Receipt(receiptProps);
        Assert.assertNotNull(receipt.props.receiptId);
        Assert.assertEquals(receipt.props.receiptId, stringPaymentId);
    }
}
