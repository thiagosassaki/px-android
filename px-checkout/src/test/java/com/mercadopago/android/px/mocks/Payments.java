package com.mercadopago.android.px.mocks;

import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.utils.ResourcesUtil;

public class Payments {
    public static Payment getApprovedPayment() {
        String json = ResourcesUtil.getStringResource("approved_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getRejectedPayment() {
        String json = ResourcesUtil.getStringResource("rejected_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getPendingPayment() {
        String json = ResourcesUtil.getStringResource("pending_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static Payment getCallForAuthPayment() {
        String json = ResourcesUtil.getStringResource("call_for_auth_payment.json");
        return JsonUtil.getInstance().fromJson(json, Payment.class);
    }

    public static ApiException getInvalidESCPayment() {
        String json = ResourcesUtil.getStringResource("invalid_esc_payment.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }

    public static ApiException getInvalidIdentificationPayment() {
        String json = ResourcesUtil.getStringResource("invalid_identification_payment.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }
}
