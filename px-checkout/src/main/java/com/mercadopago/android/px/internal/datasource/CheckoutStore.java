package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.internal.features.hooks.CheckoutHooks;
import com.mercadopago.android.px.internal.features.hooks.Hook;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class CheckoutStore {

    private static final CheckoutStore INSTANCE = new CheckoutStore();

    private CheckoutHooks checkoutHooks;

    //App state
    private Hook hook;
    private final Map<String, Object> data;

    private CheckoutStore() {
        data = new HashMap<>();
        checkoutHooks = null;
    }

    public static CheckoutStore getInstance() {
        return INSTANCE;
    }

    public Hook getHook() {
        return hook;
    }

    public void setHook(final Hook hook) {
        this.hook = hook;
    }

    public CheckoutHooks getCheckoutHooks() {
        return checkoutHooks;
    }

    public Map<String, Object> getData() {
        return data;
    }
}