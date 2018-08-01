package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import com.mercadopago.android.px.core.CheckoutLazyBuilder;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.customviews.MPButton;
import com.mercadopago.android.px.services.core.Settings;
import com.mercadopago.android.px.tracking.constants.TrackingEnvironments;
import com.mercadopago.android.px.utils.ExamplesUtils;
import com.mercadopago.example.R;

import static com.mercadopago.android.px.utils.ExamplesUtils.resolveCheckoutResult;

public class CheckoutExampleActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private View mRegularLayout;
    private MPButton continueSimpleCheckout;
    private CheckoutLazyBuilder checkoutLazyBuilder;
    private static final int REQ_CODE_CHECKOUT = 1;
    private static final int REQ_CODE_JSON = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
            .build());

        Settings.setTrackingEnvironment(TrackingEnvironments.STAGING);

        setContentView(R.layout.activity_checkout_example);
        mProgressBar = findViewById(R.id.progressBar);
        mRegularLayout = findViewById(R.id.regularLayout);

        final View jsonConfigurationButton = findViewById(R.id.jsonConfigButton);
        continueSimpleCheckout = findViewById(R.id.continueButton);

        final View selectCheckoutButton = findViewById(R.id.select_checkout);

        jsonConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonInput();
            }
        });

        selectCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(CheckoutExampleActivity.this, SelectCheckoutActivity.class));
            }
        });

        checkoutLazyBuilder = new CheckoutLazyBuilder(ExamplesUtils.createBase()) {

            @Override
            public void fail(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
                continueSimpleCheckout.setEnabled(true);
                continueSimpleCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mercadoPagoCheckout.startPayment(CheckoutExampleActivity.this, REQ_CODE_CHECKOUT);
                    }
                });
            }

            @Override
            public void success(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
                continueSimpleCheckout.setEnabled(true);
                continueSimpleCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mercadoPagoCheckout.startPayment(CheckoutExampleActivity.this, REQ_CODE_CHECKOUT);
                    }
                });
            }
        };
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        resolveCheckoutResult(this, requestCode, resultCode, data, REQ_CODE_CHECKOUT);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        showRegularLayout();
        continueSimpleCheckout.setEnabled(false);
        checkoutLazyBuilder.cancel();
        checkoutLazyBuilder.fetch(this);
    }

    private void showRegularLayout() {
        mProgressBar.setVisibility(View.GONE);
        mRegularLayout.setVisibility(View.VISIBLE);
    }

    private void startJsonInput() {
        Intent intent = new Intent(this, JsonSetupActivity.class);
        startActivityForResult(intent, REQ_CODE_JSON);
    }
}