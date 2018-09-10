package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadopago.android.px.R;

public class ButtonPrimary extends Button {

    public ButtonPrimary(final Props props, final Actions callBack) {
        super(props, callBack);
    }

    @Override
    public MeliButton getButtonView(@NonNull final Context context) {
        final MeliButton view = new MeliButton(context);
        view.setId(R.id.px_button_primary);
        return view;
    }
}
