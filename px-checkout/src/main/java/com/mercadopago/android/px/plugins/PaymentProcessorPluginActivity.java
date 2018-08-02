package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.components.Action;
import com.mercadopago.android.px.components.ActionDispatcher;
import com.mercadopago.android.px.components.ComponentManager;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.plugins.model.Processor;

public final class PaymentProcessorPluginActivity extends AppCompatActivity implements ActionDispatcher, Processor {

    private static final String EXTRA_BUSINESS_PAYMENT = "extra_business_payment";

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, PaymentProcessorPluginActivity.class);
    }

    public static boolean isBusiness(@Nullable Intent intent) {
        return intent != null && intent.getExtras() != null && intent.getExtras().containsKey(EXTRA_BUSINESS_PAYMENT);
    }

    public static BusinessPayment getBusinessPayment(Intent intent) {
        return (BusinessPayment) intent.getExtras().get(EXTRA_BUSINESS_PAYMENT);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CheckoutStore store = CheckoutStore.getInstance();
        final Session session = Session.getSession(getApplicationContext());
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final UserSelectionRepository userSelectionRepository = configurationModule.getUserSelectionRepository();
        final PaymentProcessor paymentProcessor =
            store.doesPaymentProcessorSupportPaymentMethodSelected(
                userSelectionRepository.getPaymentMethod().getId()
            );

        if (paymentProcessor == null) {
            cancel();
            return;
        }

//        final PaymentComponent.Props props = new PaymentComponent.Props.Builder()
//            .setData(store.getData())
//            .setPaymentData(store.getPaymentData())
//            .setCheckoutPreference(store.getCheckoutPreference())
//            .build();
//
//        final PaymentComponent component = paymentProcessor.initPayment(props, this);
//        final ComponentManager componentManager = new ComponentManager(this);
//
//        component.setDispatcher(this);
//        componentManager.render(component);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof PaymentPluginProcessorResultAction) {
            final PluginPayment pluginResult = ((PaymentPluginProcessorResultAction) action).getPluginPaymentResult();
            pluginResult.process(this);
        } else {
            throw new UnsupportedOperationException("Not action with payment processor plugin");
        }
    }

    @Override
    public void process(final BusinessPayment businessPayment) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BUSINESS_PAYMENT, businessPayment);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void process(final GenericPayment genericPayment) {
        final PaymentResult paymentResult = toPaymentResult(genericPayment);
        CheckoutStore.getInstance().setPaymentResult(paymentResult);
        setResult(RESULT_OK);
        finish();
    }

    private PaymentResult toPaymentResult(@NonNull final GenericPayment genericPayment) {

        final Payment payment = new Payment();
        payment.setId(genericPayment.paymentId);
        payment.setPaymentMethodId(genericPayment.paymentData.getPaymentMethod().getId());
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(genericPayment.status);
        payment.setStatusDetail(genericPayment.statusDetail);

        return new PaymentResult.Builder()
            .setPaymentData(genericPayment.paymentData)
            .setPaymentId(payment.getId())
            .setPaymentStatus(payment.getStatus())
            .setPaymentStatusDetail(payment.getStatusDetail())
            .build();
    }
}