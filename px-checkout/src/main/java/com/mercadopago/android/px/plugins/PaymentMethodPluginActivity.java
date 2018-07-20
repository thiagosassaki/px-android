package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.components.Action;
import com.mercadopago.android.px.components.ActionDispatcher;
import com.mercadopago.android.px.components.BackAction;
import com.mercadopago.android.px.components.Component;
import com.mercadopago.android.px.components.ComponentManager;
import com.mercadopago.android.px.components.NextAction;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.datasource.PluginService;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.plugins.model.PaymentMethodInfo;
import com.mercadopago.android.px.tracker.FlowHandler;
import com.mercadopago.android.px.tracker.MPTrackingContext;
import com.mercadopago.android.px.tracking.model.ScreenViewEvent;

public class PaymentMethodPluginActivity extends AppCompatActivity implements ActionDispatcher {

    private static final String SCREEN_NAME_CONFIG_PAYMENT_METHOD_PLUGIN = "CONFIG_PAYMENT_METHOD";
    private static final String PUBLIC_KEY = "public_key";

    private String mPublicKey;

    public static Intent getIntent(@NonNull final Context context, @NonNull final String publicKey) {
        final Intent intent = new Intent(context, PaymentMethodPluginActivity.class);
        intent.putExtra(PUBLIC_KEY, publicKey);
        return intent;
    }

    ComponentManager componentManager;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Session session = Session.getSession(this);
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final UserSelectionRepository userSelectionRepository = configurationModule.getUserSelectionRepository();
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();

        if (paymentMethod == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final PaymentMethodInfo paymentMethodInfo = new PluginService(this)
            .getPaymentMethodInfo(paymentMethod.getId());

        final PaymentMethodPlugin plugin = CheckoutStore
            .getInstance().getPaymentMethodPluginById(paymentMethodInfo.getId());

        final Intent intent = getIntent();
        mPublicKey = intent.getStringExtra(PUBLIC_KEY);

        trackScreen(plugin.getId());

        final PluginComponent.Props props = new PluginComponent.Props.Builder()
            .setData(CheckoutStore.getInstance().getData())
            .setCheckoutPreference(configurationModule.getPaymentSettings().getCheckoutPreference())
            .build();

        final Component component = plugin.createConfigurationComponent(props, this);
        componentManager = new ComponentManager(this);

        if (component == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        component.setDispatcher(this);
        componentManager.render(component);
    }

    private void trackScreen(final String id) {

        final String screenName = SCREEN_NAME_CONFIG_PAYMENT_METHOD_PLUGIN + "_" + id;

        final MPTrackingContext mTrackingContext = new MPTrackingContext.Builder(this, mPublicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        final ScreenViewEvent event = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(screenName)
            .setScreenName(screenName)
            .build();

        mTrackingContext.trackEvent(event);
    }

    @Override
    public void dispatch(final Action action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (action instanceof NextAction) {
                    setResult(RESULT_OK);
                    finish();
                } else if (action instanceof BackAction) {
                    onBackPressed();
                } else {
                    componentManager.dispatch(action);
                }
            }
        });
    }
}