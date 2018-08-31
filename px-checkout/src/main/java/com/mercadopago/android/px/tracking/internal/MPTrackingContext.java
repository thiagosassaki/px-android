package com.mercadopago.android.px.tracking.internal;

import android.content.Context;
import android.os.Build;
import com.mercadopago.android.px.model.AppInformation;
import com.mercadopago.android.px.model.DeviceInfo;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.Fingerprint;

/* default */ class MPTrackingContext {

    private Context context;
    private String publicKey;
    private AppInformation appInformation;
    private DeviceInfo deviceInfo;
    private String trackingStrategy;

    private MPTrackingContext(final Builder builder) {
        context = builder.context;
        publicKey = builder.publicKey;
        deviceInfo = initializeDeviceInfo();
        trackingStrategy = builder.trackingStrategy;

        if (!builder.publicKey.isEmpty() && builder.version != null) {
            appInformation = initializeAppInformation(builder.version);
        }
    }

    private AppInformation initializeAppInformation(final String version) {
        return new AppInformation.Builder()
            .setVersion(version)
            .setPlatform("mobile/android")
            .build();
    }

    private DeviceInfo initializeDeviceInfo() {
        return new DeviceInfo.Builder()
            .setModel(Build.MODEL)
            .setOS("android")
            .setUuid(Fingerprint.getAndroidId(this.context))
            .setSystemVersion(Fingerprint.getDeviceSystemVersion())
            .setScreenSize(Fingerprint.getDeviceResolution(this.context))
            .setResolution(String.valueOf(Fingerprint.getDeviceScreenDensity(this.context)))
            .build();
    }

    /* default */ void trackEvent(Event event) {
        MPTracker.getInstance().trackEvent(publicKey, appInformation, deviceInfo, event, context, trackingStrategy);
    }

    /* default */ void clearExpiredTracks() {
        MPTracker.getInstance().clearExpiredTracks();
    }

    /* default */ static class Builder {
        private Context context;
        private String publicKey;
        private String version;
        private String trackingStrategy;

        /* default */ Builder(Context context, String publicKey) {
            this.context = context;
            this.publicKey = publicKey;
        }

        /* default */ Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        /* default */ Builder setTrackingStrategy(String trackingStrategy) {
            this.trackingStrategy = trackingStrategy;
            return this;
        }

        /* default */ MPTrackingContext build() {
            return new MPTrackingContext(this);
        }
    }
}
