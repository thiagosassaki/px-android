package com.mercadopago.android.px.tracking.internal;

public final class Settings {

    private static String trackingEnvironment = TrackingEnvironments.PRODUCTION;
    public static String eventsTrackingVersion = "2";
    public static String servicesVersion = "v1";

    private Settings() {
        //DO nothing
    }

    public static void setTrackingEnvironment(final String mode) {
        trackingEnvironment = mode;
    }

    public static String getTrackingEnvironment() {
        return trackingEnvironment;
    }

}