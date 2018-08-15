package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

/**
 * Created by vaserber on 1/20/17.
 */

public interface ReturnCallback {
    void onCancel();

    void onFailure(MercadoPagoError error);
}
