package com.mercadopago.android.px.internal.features.hooks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.internal.datasource.CheckoutStore;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.BackAction;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.ComponentManager;
import com.mercadopago.android.px.internal.view.NextAction;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.Action;

public class HookActivity extends AppCompatActivity implements ActionDispatcher {

    public static Intent getIntent(@NonNull final Context context, @NonNull final Hook hook) {
        CheckoutStore.getInstance().setHook(hook);
        final Intent intent = new Intent(context, HookActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RendererFactory.register(HookComponent.class, HookRenderer.class);

        final ComponentManager componentManager = new ComponentManager(this);
        final Hook hook = CheckoutStore.getInstance().getHook();

        if (hook == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final Component component = hook.createComponent();
        component.setDispatcher(this);
        componentManager.render(component);
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof NextAction) {
            setResult(RESULT_OK);
            finish();
        } else if (action instanceof BackAction) {
            onBackPressed();
        }
    }
}