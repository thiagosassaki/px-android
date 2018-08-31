package com.mercadopago.android.px.internal.view;

import com.mercadopago.android.px.model.Action;

/**
 * Created by vaserber on 11/14/17.
 */

public class LinkAction extends Action {

    public final String url;

    public LinkAction(final String url) {
        this.url = url;
    }
}
