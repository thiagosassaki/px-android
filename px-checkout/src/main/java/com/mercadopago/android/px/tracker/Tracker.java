package com.mercadopago.android.px.tracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.mercadopago.android.px.BuildConfig;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.CardPaymentMetadata;
import com.mercadopago.android.px.model.OneTapMetadata;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.review_and_confirm.models.PaymentModel;
import com.mercadopago.android.px.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.tracking.model.ActionEvent;
import com.mercadopago.android.px.tracking.model.ScreenViewEvent;
import com.mercadopago.android.px.tracking.utils.TrackingUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Tracker {

    private static String trackingStrategy = TrackingUtil.NOOP_STRATEGY;

    private static void addProperties(final ScreenViewEvent.Builder builder,
        final List<Pair<String, String>> propertyList) {
        if (propertyList == null) {
            return;
        } else {
            for (Pair<String, String> property : propertyList) {
                builder.addProperty(property.first, property.second);
            }
        }
    }

    private static MPTrackingContext getTrackerContext(final String merchantPublicKey, final Context context) {
        MPTrackingContext.Builder builder = new MPTrackingContext.Builder(context, merchantPublicKey)
            .setVersion(BuildConfig.VERSION_NAME);
        if (trackingStrategy.equals(TrackingUtil.REALTIME_STRATEGY)) {
            builder.setTrackingStrategy(TrackingUtil.REALTIME_STRATEGY);
            trackingStrategy = TrackingUtil.NOOP_STRATEGY;
        }

        return builder.build();
    }

    public static void trackScreen(final String screenId,
        final String screenName,
        final String merchantPublicKey,
        final Context context) {
        trackScreen(screenId, screenName, context, merchantPublicKey, new ArrayList<Pair<String, String>>());
    }

    public static void trackScreen(final String screenId,
        final String screenName,
        final Context context,
        final String merchantPublicKey,
        final List<Pair<String, String>> properties) {

        MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);

        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(screenId)
            .setScreenName(screenName);

        addProperties(builder, properties);
        ScreenViewEvent event = builder.build();
        mpTrackingContext.trackEvent(event);
    }

    public static void trackReviewAndConfirmScreen(final Context context,
        final String merchantPublicKey,
        final PaymentModel paymentModel) {

        List<Pair<String, String>> properties = new ArrayList<>();
        properties.add(new Pair<>(TrackingUtil.PROPERTY_SHIPPING_INFO, TrackingUtil.HAS_SHIPPING_DEFAULT_VALUE));
        properties.add(new Pair<>(TrackingUtil.PROPERTY_PAYMENT_TYPE_ID, paymentModel.getPaymentType()));
        properties.add(new Pair<>(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID, paymentModel.paymentMethodId));
        properties.add(new Pair<>(TrackingUtil.PROPERTY_ISSUER_ID, String.valueOf(paymentModel.issuerId)));

        trackScreen(TrackingUtil.SCREEN_ID_REVIEW_AND_CONFIRM,
            TrackingUtil.SCREEN_NAME_REVIEW_AND_CONFIRM,
            context, merchantPublicKey, properties);
    }

    public static void trackOneTapScreen(@NonNull final Context context, @NonNull final String merchantPublicKey,
        @NonNull final OneTapMetadata oneTapMetadata, @NonNull final BigDecimal transactionAmount) {
        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);

        final ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_ONE_TAP)
            .setScreenName(TrackingUtil.SCREEN_ID_ONE_TAP)
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_TYPE_ID, oneTapMetadata.getPaymentTypeId())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID, oneTapMetadata.getPaymentMethodId())
            .addProperty(TrackingUtil.PROPERTY_PURCHASE_AMOUNT, transactionAmount.toString());

        if (oneTapMetadata.getCard() != null) {
            builder.addProperty(TrackingUtil.PROPERTY_INSTALLMENTS,
                oneTapMetadata.getCard().getAutoSelectedInstallment().getInstallments().toString());
            builder.addProperty(TrackingUtil.PROPERTY_CARD_ID, oneTapMetadata.getCard().getId());
        }

        mpTrackingContext.trackEvent(builder.build());
    }

    public static void trackOneTapConfirm(@NonNull final Context context,
        @NonNull final String merchantPublicKey,
        @NonNull final OneTapMetadata oneTapMetadata,
        @NonNull final BigDecimal totalAmount) {
        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);

        final ActionEvent.Builder builder = new ActionEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setAction(TrackingUtil.ACTION_CHECKOUT_CONFIRMED)
            .setScreenId(TrackingUtil.SCREEN_ID_ONE_TAP)
            .setScreenName(TrackingUtil.SCREEN_ID_ONE_TAP)
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_TYPE_ID, oneTapMetadata.getPaymentTypeId())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID, oneTapMetadata.getPaymentMethodId())
            .addProperty(TrackingUtil.PROPERTY_PURCHASE_AMOUNT, totalAmount.toString());

        final CardPaymentMetadata card = oneTapMetadata.getCard();

        if (card != null) {
            builder.addProperty(TrackingUtil.PROPERTY_INSTALLMENTS,
                card.getAutoSelectedInstallment().getInstallments().toString());
            builder.addProperty(TrackingUtil.PROPERTY_CARD_ID, card.getId());
        }

        mpTrackingContext.trackEvent(builder.build());
    }

    public static void trackOneTapCancel(@NonNull final Context context, @NonNull final String merchantPublicKey) {
        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);

        final ActionEvent.Builder builder = new ActionEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setAction(TrackingUtil.ACTION_CANCEL_ONE_TAP)
            .setScreenId(TrackingUtil.SCREEN_ID_ONE_TAP)
            .setScreenName(TrackingUtil.SCREEN_ID_ONE_TAP);

        mpTrackingContext.trackEvent(builder.build());
    }

    public static void trackOneTapSummaryDetail(@NonNull final Context context,
        @NonNull final String merchantPublicKey, final boolean hasDiscount, final CardPaymentMetadata card) {
        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);
        final ActionEvent.Builder builder = new ActionEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setAction(TrackingUtil.ACTION_OPEN_SUMMARY_ONE_TAP)
            .setScreenId(TrackingUtil.SCREEN_ID_ONE_TAP)
            .setScreenName(TrackingUtil.SCREEN_ID_ONE_TAP)
            .addProperty(TrackingUtil.PROPERTY_HAS_DISCOUNT, String.valueOf(hasDiscount));

        if (card != null) {
            builder.addProperty(TrackingUtil.PROPERTY_INSTALLMENTS,
                card.getAutoSelectedInstallment().getInstallments().toString());
        }

        mpTrackingContext.trackEvent(builder.build());
    }

    public static void trackDiscountTermsAndConditions(@NonNull final Context context,
        @NonNull final String merchantPublicKey) {
        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);
        final ActionEvent.Builder builder = new ActionEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setScreenId(TrackingUtil.SCREEN_ID_DISCOUNT_TERMS)
            .setScreenName(TrackingUtil.SCREEN_ID_DISCOUNT_TERMS);

        mpTrackingContext.trackEvent(builder.build());
    }

    public static void trackCheckoutConfirm(final Context context, final String merchantPublicKey,
        final PaymentModel paymentModel, final SummaryModel summaryModel) {

        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        final MPTrackingContext mpTrackingContext = getTrackerContext(merchantPublicKey, context);

        ActionEvent.Builder builder = new ActionEvent.Builder()
            .setFlowId(FlowHandler.getInstance().getFlowId())
            .setAction(TrackingUtil.ACTION_CHECKOUT_CONFIRMED)
            .setScreenId(TrackingUtil.SCREEN_ID_REVIEW_AND_CONFIRM)
            .setScreenName(TrackingUtil.SCREEN_NAME_REVIEW_AND_CONFIRM)
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_TYPE_ID, paymentModel.getPaymentType())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID, paymentModel.paymentMethodId)
            .addProperty(TrackingUtil.PROPERTY_PURCHASE_AMOUNT, summaryModel.getAmountToPay().toString());

        if (PaymentTypes.isCreditCardPaymentType(summaryModel.getPaymentTypeId())) {
            builder.addProperty(TrackingUtil.PROPERTY_INSTALLMENTS, String.valueOf(summaryModel.getInstallments()));
        }

        //If is saved card
        String cardId = paymentModel.getCardId();
        if (cardId != null) {
            builder.addProperty(TrackingUtil.PROPERTY_CARD_ID, cardId);
        }

        final ActionEvent actionEvent = builder.build();
        mpTrackingContext.trackEvent(actionEvent);
    }

    public static void trackPaymentVaultScreen(final Context context,
        final String merchantPublicKey,
        final PaymentMethodSearch paymentMethodSearch,
        final Set<String> escCardIds) {

        trackingStrategy = TrackingUtil.REALTIME_STRATEGY;

        List<Pair<String, String>> properties = new ArrayList<>();
        properties.add(new Pair<>(TrackingUtil.PROPERTY_OPTIONS,
            getFormattedPaymentMethodsForTracking(context, paymentMethodSearch, escCardIds)));

        trackScreen(TrackingUtil.SCREEN_ID_PAYMENT_VAULT,
            TrackingUtil.SCREEN_NAME_PAYMENT_VAULT,
            context, merchantPublicKey, properties);
    }

    public static void trackPaymentVaultChildrenScreen(@NonNull final Context context,
        @NonNull final String merchantPublicKey,
        @NonNull final PaymentMethodSearchItem selectedItem) {

        String selectedItemId = selectedItem.getId();

        if (TrackingUtil.GROUP_TICKET.equals(selectedItemId)) {
            trackScreen(TrackingUtil.SCREEN_ID_PAYMENT_VAULT_TICKET, TrackingUtil.SCREEN_NAME_PAYMENT_VAULT_TICKET,
                context, merchantPublicKey, null);
        } else if (TrackingUtil.GROUP_BANK_TRANSFER.equals(selectedItemId)) {
            trackScreen(TrackingUtil.SCREEN_ID_PAYMENT_VAULT_BANK_TRANSFER,
                TrackingUtil.SCREEN_NAME_PAYMENT_VAULT_BANK_TRANSFER, context, merchantPublicKey, null);
        } else if (TrackingUtil.GROUP_CARDS.equals(selectedItemId)) {
            trackScreen(TrackingUtil.SCREEN_ID_PAYMENT_VAULT_CARDS, TrackingUtil.SCREEN_NAME_PAYMENT_VAULT_CARDS,
                context, merchantPublicKey, null);
        } else {
            trackScreen(TrackingUtil.SCREEN_ID_PAYMENT_VAULT, TrackingUtil.SCREEN_NAME_PAYMENT_VAULT, context,
                merchantPublicKey, null);
        }
    }

    private static String getFormattedPaymentMethodsForTracking(final Context context,
        @NonNull final PaymentMethodSearch paymentMethodSearch, final Set<String> escCardIds) {
        final Collection<PaymentMethodPlugin> paymentMethodPluginList =
            Session.getSession(context).getPluginRepository().getPaymentMethodPluginList();
        return TrackingFormatter
            .getFormattedPaymentMethodsForTracking(paymentMethodSearch, paymentMethodPluginList, escCardIds);
    }
}
