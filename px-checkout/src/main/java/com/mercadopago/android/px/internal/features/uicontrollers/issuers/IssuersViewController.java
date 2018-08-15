package com.mercadopago.android.px.internal.features.uicontrollers.issuers;

import android.view.View;
import com.mercadopago.android.px.internal.features.uicontrollers.CustomViewController;
import com.mercadopago.android.px.model.Issuer;

/**
 * Created by vaserber on 10/11/16.
 */

public interface IssuersViewController extends CustomViewController {
    void drawIssuer(Issuer issuer);

    void setOnClickListener(View.OnClickListener listener);
}
