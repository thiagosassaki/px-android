package com.mercadopago.android.px.internal.tracker;

import android.content.Context;
import android.os.Build;
import com.mercadopago.android.px.model.AppInformation;
import com.mercadopago.android.px.model.DeviceInfo;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.Fingerprint;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.Settings;

/**
 * Created by vaserber on 6/5/17.
 */

public final class MPTrackingContext {

    private final String publicKey;
    private final Context context;
    private AppInformation appInformation;
    private final DeviceInfo deviceInfo;
    private final String trackingStrategy;

    private MPTrackingContext(final Builder builder) {
        context = builder.context;
        deviceInfo = initializeDeviceInfo();
        trackingStrategy = builder.trackingStrategy;
        publicKey = builder.publicKey;

        if (!builder.publicKey.isEmpty() && builder.version != null) {
            appInformation = initializeAppInformation(builder.version);
        }
    }

    private AppInformation initializeAppInformation(final String version) {
        return new AppInformation.Builder()
            .setVersion(version)
            .setPlatform("/mobile/android")
            .setEnvironment(Settings.getTrackingEnvironment())
            .build();
    }

    private DeviceInfo initializeDeviceInfo() {
        return new DeviceInfo.Builder()
            .setModel(Build.MODEL)
            .setOS("android")
            .setUuid(Fingerprint.getAndroidId(context))
            .setSystemVersion(Fingerprint.getDeviceSystemVersion())
            .setScreenSize(Fingerprint.getDeviceResolution(context))
            .setResolution(String.valueOf(Fingerprint.getDeviceScreenDensity(context)))
            .build();
    }

    public void trackEvent(final Event event) {
        MPTracker.getInstance().trackEvent(publicKey, appInformation, deviceInfo, event, context, trackingStrategy);
    }

    public void clearExpiredTracks() {
        MPTracker.getInstance().clearExpiredTracks();
    }

    public static class Builder {
        private final Context context;
        private String publicKey;
        private String version;
        private String trackingStrategy;

        public Builder(final Context context, final String publicKey) {
            this.context = context;
            this.publicKey = publicKey;
        }

        public Builder setPublicKey(final String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setVersion(final String version) {
            this.version = version;
            return this;
        }

        public Builder setTrackingStrategy(final String trackingStrategy) {
            this.trackingStrategy = trackingStrategy;
            return this;
        }

        public MPTrackingContext build() {
            return new MPTrackingContext(this);
        }
    }
}
