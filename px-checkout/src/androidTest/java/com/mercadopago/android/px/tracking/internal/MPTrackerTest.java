package com.mercadopago.android.px.tracking.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.android.px.model.ActionEvent;
import com.mercadopago.android.px.model.AppInformation;
import com.mercadopago.android.px.model.DeviceInfo;
import com.mercadopago.android.px.model.ErrorEvent;
import com.mercadopago.android.px.model.Event;
import com.mercadopago.android.px.model.PaymentIntent;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.StackTraceInfo;
import com.mercadopago.android.px.model.TrackingIntent;
import com.mercadopago.android.px.tracking.PXEventListener;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.StrategyMode;
import com.mercadopago.android.px.tracking.internal.strategies.TrackingStrategy;
import com.mercadopago.android.px.tracking.mocks.MPMockedTrackingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MPTrackerTest {

    private static final String MOCKED_FLOW_ID = "12345";
    private static final String MOCKED_CHECKOUT_VERSION = "3.0.0";
    private static final String MOCKED_PLATFORM = "native/android";
    private static final String MOCKED_PUBLIC_KEY = "public_key";
    private static final String MOCKED_MODEL = "model";
    private static final String MOCKED_OS = "android";
    private static final String MOCKED_RESOLUTION = "resolution";
    private static final String MOCKED_SCREEN_SIZE = "size";
    private static final String MOCKED_SYSTEM_VERSION = "system_version";
    private static final String MOCKED_ACTION = "action";
    private static final String MOCKED_CATEGORY = "category";
    private static final String MOCKED_LABEL = "label";
    private static final String MOCKED_VALUE = "value";

    private static final String MOCKED_SCREEN_ID_1 = "id_1";
    private static final String MOCKED_SCREEN_NAME_1 = "name_1";
    private static final String MOCKED_ERROR_CLASS_1 = "class_1";
    private static final String MOCKED_ERROR_MESSAGE_1 = "message_1";

    private static final String MOCKED_PROPERTY_KEY_1 = "property_key_1";
    private static final String MOCKED_PROPERTY_VALUE_1 = "property_value_1";

    private static final String ACTION_EVENT_KEY_ACTION = "action";
    private static final String ACTION_EVENT_KEY_CATEGORY = "category";
    private static final String ACTION_EVENT_KEY_LABEL = "label";
    private static final String ACTION_EVENT_KEY_VALUE = "value";
    private static final String ACTION_EVENT_KEY_SCREEN_ID = "screen_id";
    private static final String ACTION_EVENT_KEY_SCREEN_NAME = "screen_name";

    private static final Long MOCKED_PAYMENT_ID = 123L;
    private static final String MOCKED_PAYMENT_TYPE_ID = "pm_type_id";

    private static final String MOCKED_SITE_ID = "site_id";
    private static final String MOCKED_PAYMENT_PLATFORM = "Android";
    private static final String MOCKED_SDK_TYPE = "native";

    private static final String MOCKED_TOKEN_ID = "1234";

    private static final String MOCKED_STACK_TRACE_FILE = "file_name";
    private static final Integer MOCKED_STACK_TRACE_LINE = 123;
    private static final Integer MOCKED_STACK_TRACE_COLUMN = 3;
    private static final String MOCKED_STACK_TRACE_METHOD = "method_name";
    private static final String MOCKED_SCREEN_REVIEW_AND_CONFIRM = "Review and confirm";

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    private AppInformation getAppInformation() {
        return new AppInformation.Builder()
            .setVersion(MOCKED_CHECKOUT_VERSION)
            .setPlatform(MOCKED_PLATFORM)
            .build();
    }

    private DeviceInfo getDeviceInfo() {
        return new DeviceInfo.Builder()
            .setModel(MOCKED_MODEL)
            .setOS(MOCKED_OS)
            .setResolution(MOCKED_RESOLUTION)
            .setScreenSize(MOCKED_SCREEN_SIZE)
            .setSystemVersion(MOCKED_SYSTEM_VERSION)
            .build();
    }

    @Test
    public void sendScreenViewEventTrack() {
        final AppInformation appInformation = getAppInformation();
        final DeviceInfo deviceInfo = getDeviceInfo();

        final Event screenViewEvent = new ScreenViewEvent.Builder()
            .setFlowId(MOCKED_FLOW_ID)
            .setScreenId(MOCKED_SCREEN_ID_1)
            .setScreenName(MOCKED_SCREEN_REVIEW_AND_CONFIRM)
            .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new PXEventListener() {
            @Override
            public void onScreenLaunched(@NonNull final String screenName, @Nullable final Map extraParams) {
                assertEquals(((ScreenViewEvent) screenViewEvent).getScreenName(), screenName);
            }

            @Override
            public void onEvent(@NonNull final Object event) {

            }
        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, screenViewEvent, context,
            StrategyMode.BATCH_STRATEGY);

        final TrackingStrategy strategy = MPTracker.getInstance().getTrackingStrategy();

        assertEquals(strategy.getAppInformation(), appInformation);
        assertEquals(strategy.getDeviceInfo(), deviceInfo);
    }

    @Test
    public void sendScreenViewEventWithExtraParamsInTrack() {
        final AppInformation appInformation = getAppInformation();
        final DeviceInfo deviceInfo = getDeviceInfo();

        final Event screenViewEvent = new ScreenViewEvent.Builder()
            .setFlowId(MOCKED_FLOW_ID)
            .setScreenId(MOCKED_SCREEN_ID_1)
            .setScreenName(MOCKED_SCREEN_REVIEW_AND_CONFIRM)
            .addProperty(MOCKED_PROPERTY_KEY_1, MOCKED_PROPERTY_VALUE_1)
            .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new PXEventListener<HashMap<String, String>>() {

            @Override
            public void onScreenLaunched(@NonNull final String screenName,
                @NonNull final Map<String, String> extraParams) {
                assertNotNull(extraParams);
                assertEquals(1, extraParams.size());
                assertEquals(MOCKED_PROPERTY_VALUE_1, extraParams.get(MOCKED_PROPERTY_KEY_1));
            }

            @Override
            public void onEvent(@NonNull final HashMap<String, String> event) {

            }
        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, screenViewEvent, context,
            StrategyMode.BATCH_STRATEGY);
    }

    @Test
    public void sendActionEventTrack() {
        final AppInformation appInformation = getAppInformation();
        final DeviceInfo deviceInfo = getDeviceInfo();

        Event actionEvent = new ActionEvent.Builder()
            .setAction(MOCKED_ACTION)
            .setCategory(MOCKED_CATEGORY)
            .setLabel(MOCKED_LABEL)
            .setValue(MOCKED_VALUE)
            .setScreenId(MOCKED_SCREEN_ID_1)
            .setScreenName(MOCKED_SCREEN_NAME_1)
            .addProperty(MOCKED_PROPERTY_KEY_1, MOCKED_PROPERTY_VALUE_1)
            .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().setTracksListener(new PXEventListener<HashMap>() {
            @Override
            public void onScreenLaunched(@NonNull final String screenName, @Nullable final Map extraParams) {

            }

            @Override
            public void onEvent(@NonNull final HashMap eventMap) {
                assertEquals(eventMap.get(ACTION_EVENT_KEY_ACTION), MOCKED_ACTION);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_CATEGORY), MOCKED_CATEGORY);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_LABEL), MOCKED_LABEL);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_VALUE), MOCKED_VALUE);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_SCREEN_ID), MOCKED_SCREEN_ID_1);
                assertEquals(eventMap.get(ACTION_EVENT_KEY_SCREEN_NAME), MOCKED_SCREEN_NAME_1);
            }
        });

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, actionEvent, context,
            StrategyMode.BATCH_STRATEGY);
    }

    @Test
    public void sendPaymentTrack() {
        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        //Initialize tracker before creating a payment
        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        PaymentIntent paymentIntent = MPTracker.getInstance().trackPayment(MOCKED_PAYMENT_ID, MOCKED_PAYMENT_TYPE_ID);

        assertEquals(String.valueOf(MOCKED_PAYMENT_ID), paymentIntent.mPaymentId);
        assertEquals(MOCKED_PAYMENT_PLATFORM, paymentIntent.mPlatform);
        assertEquals(MOCKED_PUBLIC_KEY, paymentIntent.mPublicKey);
        assertEquals(MOCKED_CHECKOUT_VERSION, paymentIntent.mSdkVersion);
        assertEquals(MOCKED_SITE_ID, paymentIntent.mSite);
        assertEquals(MOCKED_SDK_TYPE, paymentIntent.mType);
    }

    @Test
    public void sendTokenTrack() {
        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        //Initialize tracker before creating a token
        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        final TrackingIntent trackingIntent = MPTracker.getInstance().trackToken(MOCKED_TOKEN_ID);

        assertEquals(MOCKED_PAYMENT_PLATFORM, trackingIntent.mPlatform);
        assertEquals(MOCKED_PUBLIC_KEY, trackingIntent.mPublicKey);
        assertEquals(MOCKED_CHECKOUT_VERSION, trackingIntent.mSdkVersion);
        assertEquals(MOCKED_SITE_ID, trackingIntent.mSite);
        assertEquals(MOCKED_SDK_TYPE, trackingIntent.mType);
        assertEquals(MOCKED_TOKEN_ID, trackingIntent.mCardToken);
    }

    @Test
    public void sendErrorEventWithStackTraceInfo() {
        final AppInformation appInformation = getAppInformation();
        final DeviceInfo deviceInfo = getDeviceInfo();

        final List<StackTraceInfo> stackTraceInfoList = new ArrayList<>();
        final StackTraceInfo stackTraceInfo = new StackTraceInfo(MOCKED_STACK_TRACE_FILE, MOCKED_STACK_TRACE_LINE,
            MOCKED_STACK_TRACE_COLUMN, MOCKED_STACK_TRACE_METHOD);
        stackTraceInfoList.add(stackTraceInfo);

        final ErrorEvent errorEvent = new ErrorEvent.Builder()
            .setErrorClass(MOCKED_ERROR_CLASS_1)
            .setErrorMessage(MOCKED_ERROR_MESSAGE_1)
            .setStackTraceList(stackTraceInfoList)
            .build();

        MPTracker.getInstance().initTracker(MOCKED_PUBLIC_KEY, MOCKED_SITE_ID, MOCKED_CHECKOUT_VERSION, context);

        MPTracker.getInstance().setMPTrackingService(new MPMockedTrackingService());

        MPTracker.getInstance().trackEvent(MOCKED_PUBLIC_KEY, appInformation, deviceInfo, errorEvent, context, null);

        final ErrorEvent sentEvent = (ErrorEvent) MPTracker.getInstance().getEvent();
        final List<StackTraceInfo> sentStackTraceList = sentEvent.getStackTraceList();
        assertEquals(1, sentStackTraceList.size());
        final StackTraceInfo sentStackTrace = sentStackTraceList.get(0);
        assertEquals(MOCKED_STACK_TRACE_FILE, sentStackTrace.getFile());
        assertEquals(MOCKED_STACK_TRACE_LINE, sentStackTrace.getLineNumber());
        assertEquals(MOCKED_STACK_TRACE_COLUMN, sentStackTrace.getColumnNumber());
        assertEquals(MOCKED_STACK_TRACE_METHOD, sentStackTrace.getMethod());
    }
}
