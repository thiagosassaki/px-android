package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.example.R;
import java.math.BigDecimal;

public final class PaymentUtils {

    private PaymentUtils() {
        //Do nothing
    }

    @NonNull
    public static BusinessPayment getBusinessPaymentApproved() {
        return new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
            R.drawable.px_icon_card, "Title")
            .setPrimaryButton(new ExitAction("Button Name", 23))
            .build();
    }

    @NonNull
    public static GenericPayment getGenericPaymentApprovedAccountMoney() {
        return new GenericPayment(123L, Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED);
    }

    private static PaymentData getPaymentDataWithAccountMoneyPlugin(final BigDecimal amount) {
        final PaymentData paymentData = new PaymentData();
        final PaymentMethod paymentMethod = new PaymentMethod("account_money", "Dinero en cuenta", "account_money");
        paymentData.setPaymentMethod(paymentMethod);
        paymentData.setTransactionAmount(amount);
        return paymentData;
    }
}
