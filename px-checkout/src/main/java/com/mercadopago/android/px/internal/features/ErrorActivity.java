package com.mercadopago.android.px.internal.features;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.controllers.CheckoutErrorHandler;
import com.mercadopago.android.px.internal.tracker.FlowHandler;
import com.mercadopago.android.px.internal.tracker.MPTrackingContext;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;

public class ErrorActivity extends MercadoPagoBaseActivity {

    private MercadoPagoError mMercadoPagoError;
    private String mPublicKey;
    private TextView mErrorMessageTextView;
    private View mRetryView;
    private View mExit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateErrorScreenLaunch();

        if (CheckoutErrorHandler.getInstance().hasCustomErrorLayout()) {
            setContentView(CheckoutErrorHandler.getInstance().getCustomErrorLayout());
        } else {
            setContentView(R.layout.px_activity_error);
        }

        getActivityParameters();
        if (validParameters()) {
            initializeControls();
            trackScreen();
            fillData();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private void animateErrorScreenLaunch() {
        overridePendingTransition(R.anim.px_fade_in_seamless, R.anim.px_fade_out_seamless);
    }

    private boolean validParameters() {
        return mMercadoPagoError != null;
    }

    private void getActivityParameters() {
        mMercadoPagoError = JsonUtil.getInstance()
            .fromJson(getIntent().getStringExtra(EXTRA_ERROR), MercadoPagoError.class);
        mPublicKey = getIntent().getStringExtra(ErrorUtil.PUBLIC_KEY_EXTRA);
    }

    private void trackScreen() {
        MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(getApplicationContext(), mPublicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_ERROR)
            .setScreenName(TrackingUtil.SCREEN_NAME_ERROR);

        if (mMercadoPagoError != null) {

            if (mMercadoPagoError.getApiException() != null) {

                ApiException apiException = mMercadoPagoError.getApiException();

                builder.addProperty(TrackingUtil.PROPERTY_ERROR_STATUS, String.valueOf(apiException.getStatus()));

                if (apiException.getCause() != null && !apiException.getCause().isEmpty() &&
                    apiException.getCause().get(0).getCode() != null) {
                    builder.addProperty(TrackingUtil.PROPERTY_ERROR_CODE,
                        String.valueOf(apiException.getCause().get(0).getCode()));
                }
                if (!TextUtils.isEmpty(apiException.getMessage())) {
                    builder.addProperty(TrackingUtil.PROPERTY_ERROR_MESSAGE, apiException.getMessage());
                }
            }

            if (mMercadoPagoError.getRequestOrigin() != null && !mMercadoPagoError.getRequestOrigin().isEmpty()) {
                builder.addProperty(TrackingUtil.PROPERTY_ERROR_REQUEST, mMercadoPagoError.getRequestOrigin());
            }
        }

        ScreenViewEvent event = builder.build();

        mpTrackingContext.trackEvent(event);
    }

    private void initializeControls() {
        mErrorMessageTextView = findViewById(R.id.mpsdkErrorMessage);
        mRetryView = findViewById(R.id.mpsdkErrorRetry);
        mExit = findViewById(R.id.mpsdkExit);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fillData() {
        String message;
        if (mMercadoPagoError.getApiException() != null) {
            message = ApiUtil.getApiExceptionMessage(this, mMercadoPagoError.getApiException());
        } else {
            message = mMercadoPagoError.getMessage();
        }

        mErrorMessageTextView.setText(message);

        if (mMercadoPagoError.isRecoverable()) {
            mRetryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            mRetryView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_ERROR, JsonUtil.getInstance().toJson(mMercadoPagoError));
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
