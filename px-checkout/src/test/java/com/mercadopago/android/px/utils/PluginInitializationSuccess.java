package com.mercadopago.android.px.utils;

import com.mercadopago.android.px.internal.datasource.PluginInitializationTask;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import java.util.ArrayList;

public class PluginInitializationSuccess extends PluginInitializationTask {

    public PluginInitializationSuccess() {
        super(new ArrayList<PaymentMethodPlugin>());
    }

    @Override
    public void execute(final DataInitializationCallbacks callbacks) {
        callbacks.onDataInitialized();
    }
}
