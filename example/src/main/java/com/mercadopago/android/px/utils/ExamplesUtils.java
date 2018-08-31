package com.mercadopago.android.px.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.ReviewAndConfirmConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.MercadoPagoCheckout.Builder;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.PXEventListener;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;

public final class ExamplesUtils {

    private ExamplesUtils() {
    }

    private static final String REQUESTED_CODE_MESSAGE = "Requested code: ";
    private static final String PAYMENT_WITH_STATUS_MESSAGE = "Payment with status: ";
    private static final String RESULT_CODE_MESSAGE = " Result code: ";
    private static final String DUMMY_PREFERENCE_ID = "243962506-0bb62e22-5c7b-425e-a0a6-c22d0f4758a9";
    private static final String DUMMY_PREFERENCE_ID_WITH_TWO_ITEMS = "243962506-b6476e8b-a1a4-40cb-bfec-9954bff4a143";
    private static final String DUMMY_PREFERENCE_ID_ONE_ITEM_WITH_QUANTITY =
        "243962506-ad5df092-f5a2-4b99-bcc4-7578d6e71849";
    private static final String DUMMY_PREFERENCE_ID_WITH_ITEM_LONG_TITLE =
        "243962506-4ddac80d-af86-4a4f-80e3-c4e4735ba200";
    private static final String DUMMY_PREFERENCE_ID_WITH_DECIMALS = "243962506-ad5df092-f5a2-4b99-bcc4-7578d6e71849";
    private static final String DUMMY_MERCHANT_PUBLIC_KEY = "TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee";

    public static void resolveCheckoutResult(final Activity context, final int requestCode, final int resultCode,
        final Intent data, final int reqCodeCheckout) {
        ViewUtils.showRegularLayout(context);

        if (requestCode == reqCodeCheckout) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);
                Toast.makeText(context, new StringBuilder()
                    .append(PAYMENT_WITH_STATUS_MESSAGE)
                    .append(payment), Toast.LENGTH_LONG)
                    .show();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null
                    && data.getExtras() != null
                    && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError =
                        (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    Toast.makeText(context, "Error: " + mercadoPagoError, Toast.LENGTH_LONG)
                        .show();
                } else {
                    Toast.makeText(context, new StringBuilder()
                        .append("Cancel - ")
                        .append(REQUESTED_CODE_MESSAGE)
                        .append(requestCode)
                        .append(RESULT_CODE_MESSAGE)

                        .append(resultCode), Toast.LENGTH_LONG)
                        .show();
                }
            } else {

                Toast.makeText(context, new StringBuilder()
                    .append(REQUESTED_CODE_MESSAGE)
                    .append(requestCode)
                    .append(RESULT_CODE_MESSAGE)
                    .append(resultCode), Toast.LENGTH_LONG)
                    .show();
            }
        }
    }

    public static List<Pair<String, Builder>> getOptions() {
        final List<Pair<String, Builder>> options = new ArrayList<>(BusinessSamples.getAll());
        OneTapSamples.addAll(options);
        ChargesSamples.addAll(options);
        DiscountSamples.addAll(options);
        options.add(new Pair<>("Review and Confirm - Custom exit", customExitReviewAndConfirm()));
        options.add(new Pair<>("Base flow - Tracks with listener", startBaseFlowWithTrackListener()));
        options.add(new Pair<>("All but debit card", allButDebitCard()));
        options.add(new Pair<>("Two items", createBaseWithTwoItems()));
        options.add(new Pair<>("One item with quantity", createBaseWithOneItemWithQuantity()));
        options.add(new Pair<>("Two items - Collector icon", createBaseWithTwoItemsAndCollectorIcon()));
        options.add(new Pair<>("One item - Long title", createBaseWithOneItemLongTitle()));
        options.add(new Pair<>("Differential pricing preference", createWithDifferentialPricing()));
        return options;
    }

    private static Builder allButDebitCard() {
        final CheckoutPreference.Builder builder = getBasePreferenceBuilder();

        for (final String type : PaymentTypes.getAllPaymentTypes()) {
            if (!PaymentTypes.DEBIT_CARD.equals(type)) {
                builder.addExcludedPaymentType(type);
            }
        }

        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, builder.build(), PaymentConfigurationUtils.create());
    }

    @NonNull
    private static CheckoutPreference.Builder getBasePreferenceBuilder() {
        final Item item = new Item.Builder("title", 1, new BigDecimal(10)).setDescription("description").build();

        return new CheckoutPreference.Builder(Sites.ARGENTINA, "a@a.a",
            Collections.singletonList(item));
    }

    private static Builder customExitReviewAndConfirm() {

        final ReviewAndConfirmConfiguration preferences = new ReviewAndConfirmConfiguration.Builder()
            .setTopFragment(Fragment.class, new Bundle())
            .build();

        return createBaseWithDecimals().setAdvancedConfiguration(
            new AdvancedConfiguration.Builder()
                .setReviewAndConfirmConfiguration(preferences)
                .build());
    }

    private static Builder startBaseFlowWithTrackListener() {
        MPTracker.getInstance().setTracksListener(new PXEventListener<HashMap<String, String>>() {

            @Override
            public void onScreenLaunched(@NonNull final String screenName,
                @NonNull final Map<String, String> extraParams) {
                Log.d("Screen track: ", screenName + " " + extraParams);
            }

            @Override
            public void onEvent(@NonNull final HashMap<String, String> event) {
                Log.d("Event track: ", event.toString());
            }
        });
        return createBase();
    }

    private static Builder createWithDifferentialPricing() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, "99628543-518e6477-ac0d-4f4a-8097-51c2fcc00b71");
    }

    public static Builder createBase() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID);
    }

    private static Builder createBaseWithDecimals() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID_WITH_DECIMALS);
    }

    private static Builder createBaseWithTwoItems() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID_WITH_TWO_ITEMS);
    }

    private static Builder createBaseWithOneItemWithQuantity() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID_ONE_ITEM_WITH_QUANTITY);
    }

    private static Builder createBaseWithTwoItemsAndCollectorIcon() {
        final ReviewAndConfirmConfiguration preferences = new ReviewAndConfirmConfiguration.Builder()
            .build();

        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID_WITH_TWO_ITEMS)
            .setAdvancedConfiguration(new AdvancedConfiguration.Builder()
                .setReviewAndConfirmConfiguration(preferences)
                .build());
    }

    private static Builder createBaseWithOneItemLongTitle() {
        return new Builder(DUMMY_MERCHANT_PUBLIC_KEY, DUMMY_PREFERENCE_ID_WITH_ITEM_LONG_TITLE);
    }
}