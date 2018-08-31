package com.mercadopago.android.px.mocks;

import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.utils.ResourcesUtil;

public class ApiExceptions {

    public static ApiException getPaymentInProcessException() {
        String json = ResourcesUtil.getStringResource("payment_in_process_exception.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }
}
