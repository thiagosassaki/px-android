package com.mercadopago.android.px.core;

import com.mercadopago.android.px.hooks.CheckoutHooks;
import com.mercadopago.android.px.hooks.Hook;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class CheckoutStore {

    private static final CheckoutStore INSTANCE = new CheckoutStore();

    @Deprecated
    private CheckoutHooks checkoutHooks;

    //App state
    private Hook hook;
    private final Map<String, Object> data = new HashMap<>();

    //Payment
    private PaymentResult paymentResult;
    private PaymentData paymentData;
    private Payment payment;

    private CheckoutStore() {
    }

    public static CheckoutStore getInstance() {
        return INSTANCE;
    }

    public Hook getHook() {
        return hook;
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }

    @Deprecated
    public CheckoutHooks getCheckoutHooks() {
        return checkoutHooks;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(final PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(final Payment payment) {
        this.payment = payment;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(final PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void reset() {
        paymentResult = null;
        paymentData = null;
        payment = null;
    }
}