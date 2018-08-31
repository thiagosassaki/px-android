package com.mercadopago.android.px.internal.features.uicontrollers.savedcards;

import android.view.View;
import com.mercadopago.android.px.internal.features.uicontrollers.CustomViewController;

/**
 * Created by mreverter on 5/10/16.
 */
public interface SavedCardView extends CustomViewController {
    void draw();

    void showSeparator();

    void setOnClickListener(View.OnClickListener listener);
}
