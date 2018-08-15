package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadolibre.android.ui.widgets.MeliButton;

import static com.mercadolibre.android.ui.widgets.MeliButton.Type.OPTION_PRIMARY;

public class ButtonLink extends Button {

    public ButtonLink(final Props props, final Actions callBack) {
        super(props, callBack);
    }

    @Override
    public MeliButton getButtonView(@NonNull final Context context) {
        final MeliButton button = new MeliButton(context);
        button.setType(OPTION_PRIMARY);
        return button;
    }
}
