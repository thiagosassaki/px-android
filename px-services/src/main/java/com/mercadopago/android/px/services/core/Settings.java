package com.mercadopago.android.px.services.core;

import com.mercadopago.android.px.tracking.constants.TrackingEnvironments;
import okhttp3.logging.HttpLoggingInterceptor;

public class Settings {

    public static String servicesVersion = "v1";
    public static final String PAYMENT_RESULT_API_VERSION = "1.4";
    public static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.6";

    private static String trackingEnvironment = TrackingEnvironments.PRODUCTION;

    public static void setTrackingEnvironment(final String mode) {
        trackingEnvironment = mode;
    }

    public static String getTrackingEnvironment() {
        return trackingEnvironment;
    }

    @Deprecated
    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}
