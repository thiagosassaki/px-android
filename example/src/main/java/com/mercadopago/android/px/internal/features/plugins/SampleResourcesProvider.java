package com.mercadopago.android.px.internal.features.plugins;

import android.content.Context;
import com.mercadopago.example.R;

public class SampleResourcesProvider implements SampleResources {

    private final Context context;

    public SampleResourcesProvider(final Context context) {
        this.context = context;
    }

    @Override
    public String getPasswordErrorMessage() {
        return context.getString(R.string.auth_error_password);
    }

    @Override
    public String getPasswordRequiredMessage() {
        return context.getString(R.string.auth_error_password_required);
    }
}
