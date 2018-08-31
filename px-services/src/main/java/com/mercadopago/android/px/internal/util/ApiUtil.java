package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.exceptions.ApiException;
import retrofit2.Response;

public final class ApiUtil {

    private ApiUtil() {

    }

    public static <T> ApiException getApiException(Response<T> response) {

        ApiException apiException = null;
        try {
            final String errorString = response.errorBody().string();
            apiException = JsonUtil.getInstance().fromJson(errorString, ApiException.class);
        } catch (Exception ex) {
            //Do nothing
        } finally {
            if (apiException == null) {
                apiException = new ApiException();
                apiException.setStatus(response.code());
            }
        }

        return apiException;
    }

    public static ApiException getApiException(Throwable throwable) {
        final ApiException apiException = new ApiException();
        if (throwable instanceof NoConnectivityException) {
            apiException.setStatus(StatusCodes.NO_CONNECTIVITY_ERROR);
        }

        try {
            apiException.setMessage(throwable.getMessage());
        } catch (Exception ex) {
            // do nothing
        }

        return apiException;
    }

    public static boolean checkConnection(@NonNull final Context context) {

        if (context != null) {
            try {
                boolean haveConnectedWifi = false;
                boolean haveConnectedMobile = false;
                final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (ni.isConnectedOrConnecting()) {
                            haveConnectedWifi = true;
                        }
                    }
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (ni.isConnectedOrConnecting()) {
                            haveConnectedMobile = true;
                        }
                    }
                }

                return haveConnectedWifi || haveConnectedMobile;
            } catch (Exception ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static final class StatusCodes {

        private StatusCodes() {

        }

        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int PROCESSING = 499;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_FOUND = 404;
        public static final int NO_CONNECTIVITY_ERROR = -1;
        public static final String INTERNAL_SERVER_ERROR_FIRST_DIGIT = "5";
    }

    public static final class RequestOrigin {

        private RequestOrigin() {

        }

        public static final String GET_PREFERENCE = "GET_PREFERENCE";
        public static final String PAYMENT_METHOD_SEARCH = "PAYMENT_METHOD_SEARCH";
        public static final String GET_INSTALLMENTS = "GET_INSTALLMENTS";
        public static final String GET_ISSUERS = "GET_ISSUERS";
        public static final String GET_DIRECT_DISCOUNT = "GET_DIRECT_DISCOUNT";
        public static final String GET_MERCHANT_DIRECT_DISCOUNT = "GET_MERCHANT_DIRECT_DISCOUNT";
        public static final String CREATE_PAYMENT = "CREATE_PAYMENT";
        public static final String CREATE_TOKEN = "CREATE_TOKEN";
        public static final String GET_CUSTOMER = "GET_CUSTOMER";
        public static final String GET_CODE_DISCOUNT = "GET_CODE_DISCOUNT";
        public static final String GET_CAMPAIGNS = "GET_CAMPAIGNS";
        public static final String GET_PAYMENT_METHODS = "GET_PAYMENT_METHODS";
        public static final String GET_IDENTIFICATION_TYPES = "GET_IDENTIFICATION_TYPES";
        public static final String GET_BANK_DEALS = "GET_BANK_DEALS";
        public static final String GET_INSTRUCTIONS = "GET_INSTRUCTIONS";
    }
}
