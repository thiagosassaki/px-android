package com.mercadopago.android.px.core;

import com.mercadopago.android.px.internal.callbacks.CallbackHolder;

/**
 * Created by vaserber on 1/27/17.
 */

public class CallbackHolderClearable extends CallbackHolder {

    private CallbackHolderClearable() {
        super();
    }

    public static void clear() {
        callbackHolder = null;
    }
}
