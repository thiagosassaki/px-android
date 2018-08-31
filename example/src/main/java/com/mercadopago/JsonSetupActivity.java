package com.mercadopago;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.google.gson.JsonSyntaxException;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.utils.CheckoutConfiguration;
import com.mercadopago.android.px.utils.PaymentConfigurationUtils;
import com.mercadopago.example.R;

public class JsonSetupActivity extends AppCompatActivity {

    private static final int RES_CODE = 1;

    private EditText mJsonInputEditText;
    private Button mStartCheckoutButton;
    private ImageView mStatusImageView;
    private CheckoutConfiguration mConfiguration;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_setup);
        initializeViews();
        setupJsonValidation();
        setupCheckoutStart();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeViews() {
        mJsonInputEditText = findViewById(R.id.jsonEditText);
        mStartCheckoutButton = findViewById(R.id.checkoutStartButton);
        mStatusImageView = findViewById(R.id.statusImage);

        mScrollView = findViewById(R.id.setupScrollView);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mScrollView.setFocusable(true);
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    private void setupJsonValidation() {
        mJsonInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateJson();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do something
            }
        });
    }

    private void setupCheckoutStart() {
        mStartCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCheckout();
            }
        });
    }

    private void startCheckout() {
        final MercadoPagoCheckout.Builder checkoutBuilder;

        if (!TextUtil.isEmpty(mConfiguration.getPrefId())) {
            checkoutBuilder =
                new MercadoPagoCheckout.Builder(mConfiguration.getPublicKey(), mConfiguration.getPrefId());
        } else {
            final CheckoutPreference preference = createCheckoutPreference(mConfiguration);
            checkoutBuilder = new MercadoPagoCheckout.Builder(mConfiguration.getPublicKey(), preference,
                PaymentConfigurationUtils.create());
        }

        checkoutBuilder.setAdvancedConfiguration(mConfiguration.getAdvancedConfiguration());
        final MercadoPagoCheckout checkout = checkoutBuilder.build();
        checkout.startPayment(this, RES_CODE);
    }

    private CheckoutPreference createCheckoutPreference(final CheckoutConfiguration checkoutConfiguration) {
        return new CheckoutPreference.Builder(checkoutConfiguration.getSite(), checkoutConfiguration.getPayerEmail(),
            checkoutConfiguration.getItems())
            .build();
    }

    private void validateJson() {
        boolean isOk = false;
        final String configsJson = mJsonInputEditText.getText().toString();
        try {
            mConfiguration = JsonUtil.getInstance().fromJson(configsJson, CheckoutConfiguration.class);
            if (!TextUtil.isEmpty(configsJson)) {
                isOk = true;
            }
        } catch (final JsonSyntaxException exception) {
            //Do nothing
        }
        updateSetupStatus(isOk);
    }

    private void updateSetupStatus(final boolean ok) {
        if (ok) {
            final Drawable okImage = getResources().getDrawable(R.drawable.px_ok_sign);
            okImage.setColorFilter(ContextCompat.getColor(this, R.color.examples_green), PorterDuff.Mode.SRC_ATOP);
            mStatusImageView.setImageDrawable(okImage);
            mStartCheckoutButton.setEnabled(true);
        } else {
            mStatusImageView.setImageResource(R.drawable.px_icon_error);
            mStartCheckoutButton.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        setResult(resultCode, data);
        finish();
    }
}
