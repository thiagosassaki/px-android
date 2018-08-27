package com.mercadopago.android.px.internal.view;

import com.mercadopago.android.px.model.Action;

/**
 * Created by vaserber on 10/20/17.
 */

public interface ActionDispatcher {

    void dispatch(final Action action);
}
