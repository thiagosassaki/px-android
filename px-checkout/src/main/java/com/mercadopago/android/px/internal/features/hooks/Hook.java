package com.mercadopago.android.px.internal.features.hooks;

import com.mercadopago.android.px.internal.view.Component;

public abstract class Hook {

    public abstract Component<HookComponent.Props, Void> createComponent();

    public boolean isEnabled() {
        return true;
    }
}