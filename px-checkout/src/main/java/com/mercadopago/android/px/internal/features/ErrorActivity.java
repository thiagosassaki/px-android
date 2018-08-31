package com.mercadopago.android.px.internal.features;

import android.content.Context;
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
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
import com.mercadopago.android.px.model.exceptions.ApiException;

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
        final MPTrackingContext mpTrackingContext = new MPTrackingContext.Builder(getApplicationContext(), mPublicKey)
            .setVersion(BuildConfig.VERSION_NAME)
            .build();

        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_ERROR)
            .setScreenName(TrackingUtil.SCREEN_NAME_ERROR);

        if (mMercadoPagoError != null) {
            builder = mMercadoPagoError.getErrorEvent(builder);
        }

        final ScreenViewEvent event = builder.build();

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
            message = getApiExceptionMessage(this, mMercadoPagoError.getApiException());
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

    private String getApiExceptionMessage(final Context context, final ApiException apiException) {
        String message;

        if (apiException.getCause() != null && !apiException.getCause().isEmpty()) {
            Cause cause = apiException.getCause().get(0);
            String errorCode = cause.getCode();
            switch (errorCode) {
            case ApiException.ErrorCodes.CUSTOMER_NOT_ALLOWED_TO_OPERATE:
                message = context.getString(R.string.px_customer_not_allowed_to_operate);
                break;
            case ApiException.ErrorCodes.COLLECTOR_NOT_ALLOWED_TO_OPERATE:
                message = context.getString(R.string.px_collector_not_allowed_to_operate);
                break;
            case ApiException.ErrorCodes.INVALID_USERS_INVOLVED:
                message = context.getString(R.string.px_invalid_users_involved);
                break;
            case ApiException.ErrorCodes.CUSTOMER_EQUAL_TO_COLLECTOR:
                message = context.getString(R.string.px_customer_equal_to_collector);
                break;
            case ApiException.ErrorCodes.INVALID_CARD_HOLDER_NAME:
                message = context.getString(R.string.px_invalid_card_holder_name);
                break;
            case ApiException.ErrorCodes.UNAUTHORIZED_CLIENT:
                message = context.getString(R.string.px_unauthorized_client);
                break;
            case ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND:
                message = context.getString(R.string.px_payment_method_not_found);
                break;
            case ApiException.ErrorCodes.INVALID_SECURITY_CODE:
                message = context.getString(R.string.px_invalid_security_code);
                break;
            case ApiException.ErrorCodes.SECURITY_CODE_REQUIRED:
                message = context.getString(R.string.px_security_code_required);
                break;
            case ApiException.ErrorCodes.INVALID_PAYMENT_METHOD:
                message = context.getString(R.string.px_invalid_payment_method);
                break;
            case ApiException.ErrorCodes.INVALID_CARD_NUMBER:
                message = context.getString(R.string.px_invalid_card_number);
                break;
            case ApiException.ErrorCodes.EMPTY_EXPIRATION_MONTH:
                message = context.getString(R.string.px_empty_card_expiration_month);
                break;
            case ApiException.ErrorCodes.EMPTY_EXPIRATION_YEAR:
                message = context.getString(R.string.px_empty_card_expiration_year);
                break;
            case ApiException.ErrorCodes.EMPTY_CARD_HOLDER_NAME:
                message = context.getString(R.string.px_empty_card_holder_name);
                break;
            case ApiException.ErrorCodes.EMPTY_DOCUMENT_NUMBER:
                message = context.getString(R.string.px_empty_document_number);
                break;
            case ApiException.ErrorCodes.EMPTY_DOCUMENT_TYPE:
                message = context.getString(R.string.px_empty_document_type);
                break;
            case ApiException.ErrorCodes.INVALID_PAYMENT_TYPE_ID:
                message = context.getString(R.string.px_invalid_payment_type_id);
                break;
            case ApiException.ErrorCodes.INVALID_PAYMENT_METHOD_ID:
                message = context.getString(R.string.px_invalid_payment_method);
                break;
            case ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH:
                message = context.getString(R.string.px_invalid_card_expiration_month);
                break;
            case ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_YEAR:
                message = context.getString(R.string.px_invalid_card_expiration_year);
                break;
            case ApiException.ErrorCodes.INVALID_PAYER_EMAIL:
                message = context.getString(R.string.px_invalid_payer_email);
                break;
            case ApiException.ErrorCodes.INVALID_IDENTIFICATION_NUMBER:
                message = context.getString(R.string.px_api_invalid_identification_number);
                break;
            default:
                message = context.getString(R.string.px_standard_error_message);
                break;
            }
        } else {
            message = context.getString(R.string.px_standard_error_message);
        }
        return message;
    }
}
