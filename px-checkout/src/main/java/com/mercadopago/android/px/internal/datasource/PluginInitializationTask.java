package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentMethodPlugin;

public class PluginInitializationTask {

    public static final String KEY_INIT_SUCCESS = "init_success";
    @NonNull private final Iterable<PaymentMethodPlugin> plugins;
    @NonNull private final PaymentMethodPlugin.CheckoutData checkoutData;

    private Thread taskThread;

    public PluginInitializationTask(@NonNull final Iterable<PaymentMethodPlugin> plugins, @NonNull final
    PaymentMethodPlugin.CheckoutData checkoutData) {
        this.plugins = plugins;
        this.checkoutData = checkoutData;
    }

    /* async init */
    public void execute(final DataInitializationCallbacks callbacks) {
        taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                initPlugins(callbacks);
            }
        });
        taskThread.start();
    }

    /* sync init */
    public void initPlugins(final DataInitializationCallbacks callback) {
        try {
            for (final PaymentMethodPlugin plugin : plugins) {
                plugin.init(checkoutData);
            }
            if (!taskThread.isInterrupted()) {
                callback.onDataInitialized();
            }
        } catch (final Exception e) {
            callback.onFailure(e);
        }
    }

    public void cancel() {
        if (taskThread != null && taskThread.isAlive() && !taskThread.isInterrupted()) {
            taskThread.interrupt();
        }
    }

    public interface DataInitializationCallbacks {
        void onDataInitialized();

        void onFailure(@NonNull final Exception e);
    }
}