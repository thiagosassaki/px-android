package com.mercadopago.android.px.tracking.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.model.ActionEvent;
import com.mercadopago.android.px.model.AppInformation;
import com.mercadopago.android.px.model.DeviceInfo;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.PaymentIntent;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.TrackingIntent;
import com.mercadopago.android.px.tracking.PXEventListener;
import com.mercadopago.android.px.tracking.internal.services.MPTrackingService;
import com.mercadopago.android.px.tracking.internal.services.MPTrackingServiceImpl;
import com.mercadopago.android.px.tracking.internal.strategies.BatchTrackingStrategy;
import com.mercadopago.android.px.tracking.internal.strategies.ConnectivityCheckerImpl;
import com.mercadopago.android.px.tracking.internal.strategies.EventsDatabaseImpl;
import com.mercadopago.android.px.tracking.internal.strategies.ForcedStrategy;
import com.mercadopago.android.px.tracking.internal.strategies.NoOpStrategy;
import com.mercadopago.android.px.tracking.internal.strategies.RealTimeTrackingStrategy;
import com.mercadopago.android.px.tracking.internal.strategies.TrackingStrategy;
import com.mercadopago.android.px.tracking.internal.utils.JsonConverter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class MPTracker {

    /**
     * This singleton instance is safe because session will work with
     * application context. Application context it's never leaking.
     */
    @SuppressLint("StaticFieldLeak") private static MPTracker mMPTrackerInstance;

    private EventsDatabaseImpl database;

    private PXEventListener mPXEventListener;

    private MPTrackingService mMPTrackingService;

    private String mPublicKey;
    private String mSdkVersion;
    private String mSiteId;
    private Context mContext;

    private static final String SDK_PLATFORM = "Android";
    private static final String SDK_TYPE = "native";
    private static final String DEFAULT_FLAVOUR = "3";

    private Boolean trackerInitialized = false;

    private TrackingStrategy trackingStrategy;
    private Event mEvent;

    private MPTracker() {
        //Do nothing
    }

    public static synchronized MPTracker getInstance() {
        if (mMPTrackerInstance == null) {
            mMPTrackerInstance = new MPTracker();
        }
        return mMPTrackerInstance;
    }

    private void initializeMPTrackingService() {
        if (mMPTrackingService == null) {
            mMPTrackingService = new MPTrackingServiceImpl();
        }
    }

    /* default */
    @VisibleForTesting
    TrackingStrategy getTrackingStrategy() {
        return trackingStrategy;
    }

    /* default */
    @VisibleForTesting
    void setMPTrackingService(final MPTrackingService trackingService) {
        mMPTrackingService = trackingService;
    }

    /* default */
    @VisibleForTesting
    Event getEvent() {
        return mEvent;
    }

    public void clearExpiredTracks() {
        if (isDatabaseInitialized()) {
            database.clearExpiredTracks();
        }
    }

    /**
     * Set listener to track library's screens and events in the app.
     *
     * @param PXEventListener PXEventListener implementing the tracking methods
     */
    public void setTracksListener(final PXEventListener PXEventListener) {
        mPXEventListener = PXEventListener;
    }

    /**
     * @param paymentId The payment id of a payment method off. Cannot be {@code null}.
     * @param typeId The payment type id. It has to be a card type.
     */
    public PaymentIntent trackPayment(final Long paymentId, final String typeId) {

        PaymentIntent paymentIntent = null;

        if (trackerInitialized) {
            paymentIntent = new PaymentIntent(mPublicKey, paymentId.toString(), DEFAULT_FLAVOUR, SDK_PLATFORM, SDK_TYPE,
                mSdkVersion, mSiteId);
            initializeMPTrackingService();
            mMPTrackingService.trackPaymentId(paymentIntent);
        }
        return paymentIntent;
    }

    /**
     * @param token The card token id of a payment. Cannot be {@code null}.
     */
    public TrackingIntent trackToken(final String token) {
        TrackingIntent trackingIntent = null;
        if (trackerInitialized && !isEmpty(token)) {
            trackingIntent =
                new TrackingIntent(mPublicKey, token, DEFAULT_FLAVOUR, SDK_PLATFORM, SDK_TYPE, mSdkVersion, mSiteId);
            initializeMPTrackingService();
            mMPTrackingService.trackToken(trackingIntent);
        }
        return trackingIntent;
    }

    /**
     * This method tracks a list of events in one request
     *
     * @param appInformation Info about this application and SDK integration
     * @param deviceInfo Info about the device that is using the app
     * @param event Event to track
     * @param context Application context
     */
    public void trackEvent(final String publicKey,
        final AppInformation appInformation,
        final DeviceInfo deviceInfo,
        final Event event,
        final Context context) {
        trackEvent(publicKey, appInformation, deviceInfo, event, context, StrategyMode.NOOP_STRATEGY);
    }

    /**
     * This method tracks a list of events in one request
     *
     * @param appInformation Info about this application and SDK integration
     * @param deviceInfo Info about the device that is using the app
     * @param event Event to track
     * @param context Application context
     */
    public void trackEvent(final String publicKey,
        final AppInformation appInformation,
        final DeviceInfo deviceInfo,
        final Event event,
        final Context context,
        final String trackingStrategy) {

        initializeMPTrackingService();

        mEvent = event;
        mContext = context;

        setTrackingStrategy(context, trackingStrategy);

        if (this.trackingStrategy != null) {
            this.trackingStrategy.setPublicKey(publicKey);
            this.trackingStrategy.setAppInformation(appInformation);
            this.trackingStrategy.setDeviceInfo(deviceInfo);
            this.trackingStrategy.trackEvent(event, context);

            if (this.trackingStrategy.readsEventFromDB()) {
                database.persist(event);
            }
        }

        if (event.getType().equals(Event.TYPE_ACTION)) {
            final ActionEvent actionEvent = (ActionEvent) event;
            final Map<String, Object> eventMap = createEventMap(actionEvent);
            trackEventPerformedListener(eventMap);
        } else if (event.getType().equals(Event.TYPE_SCREEN_VIEW)) {
            final ScreenViewEvent screenViewEvent = (ScreenViewEvent) event;
            trackScreenLaunchedListener(screenViewEvent.getScreenName(), screenViewEvent.getProperties());
        }
    }

    private void initializeDatabase() {
        if (!isDatabaseInitialized()) {
            database = new EventsDatabaseImpl(mContext);
        }
    }

    private boolean isDatabaseInitialized() {
        return database != null;
    }

    private Map<String, Object> createEventMap(final ActionEvent actionEvent) {

        final String eventJson = JsonConverter.getInstance().toJson(actionEvent);
        final Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> actionEventDataMap = new Gson().fromJson(eventJson, type);
        return new HashMap<>(actionEventDataMap);
    }

    /**
     * @param publicKey The public key of the merchant. Cannot be {@code null}.
     * @param siteId The site that comes in the preference. Cannot be {@code null}.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}.
     * @param context Reference to Android Context. Cannot be {@code null}.
     */
    public void initTracker(final String publicKey,
        final String siteId,
        final String sdkVersion,
        final Context context) {

        if (!isTrackerInitialized()) {
            if (areInitParametersValid(publicKey, siteId, sdkVersion, context)) {
                trackerInitialized = true;
                mPublicKey = publicKey;
                mSiteId = siteId;
                mSdkVersion = sdkVersion;
                mContext = context.getApplicationContext();
            }
        }
    }

    /**
     * @param publicKey The public key of the merchant. Cannot be {@code null}.
     * @param siteId The site that comes in the preference. Cannot be {@code null}.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}.
     * @param context Reference to Android Context. Cannot be {@code null}.
     * @return True if all parameters are valid. False if any parameter is invalid
     */
    private boolean areInitParametersValid(final String publicKey,
        final String siteId,
        final String sdkVersion,
        final Context context) {

        return !isEmpty(publicKey) && !isEmpty(sdkVersion) && !isEmpty(siteId) && context != null;
    }

    /**
     * Check if MPTracker is initialized
     *
     * @return True if is initialized. False if is not initialized.
     */
    private boolean isTrackerInitialized() {
        return mPublicKey != null && mSdkVersion != null && mSiteId != null && mContext != null;
    }

    private TrackingStrategy setTrackingStrategy(final Context context, final String strategy) {
        if (isBatchStrategy(strategy)) {
            initializeDatabase();
            trackingStrategy =
                new BatchTrackingStrategy(database, new ConnectivityCheckerImpl(context), mMPTrackingService);
        } else if (isForcedStrategy(strategy)) {
            initializeDatabase();
            trackingStrategy = new ForcedStrategy(database, new ConnectivityCheckerImpl(context), mMPTrackingService);
        } else if (isRealTimeStrategy(strategy)) {
            trackingStrategy = new RealTimeTrackingStrategy(mMPTrackingService);
        } else {
            trackingStrategy = new NoOpStrategy();
        }
        return trackingStrategy;
    }

    private boolean isForcedStrategy(final String strategy) {
        return StrategyMode.FORCED_STRATEGY.equals(strategy);
    }

    private boolean isBatchStrategy(final String strategy) {
        return StrategyMode.BATCH_STRATEGY.equals(strategy);
    }

    private boolean isRealTimeStrategy(final String strategy) {
        return StrategyMode.REALTIME_STRATEGY.equals(strategy);
    }

    private void trackScreenLaunchedListener(@NonNull final String screenName,
        @Nullable final Map<String, String> extraParams) {
        if (mPXEventListener != null) {
            mPXEventListener.onScreenLaunched(screenName, extraParams);
        }
    }

    private void trackEventPerformedListener(final Map<String, Object> eventMap) {
        if (mPXEventListener != null) {
            mPXEventListener.onEvent(eventMap);
        }
    }
}