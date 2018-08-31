package com.mercadopago.android.px.internal.core;

public final class Settings {

    private Settings() {
        //Do nothing
    }

    public static String servicesVersion = "v1";
    public static final String PAYMENT_RESULT_API_VERSION = "1.4";
    public static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.6";

    @Deprecated
    public static void enableBetaServices() {
        servicesVersion = "beta";
    }
}