package com.mercadopago.android.px.internal.features.review_and_confirm;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.ReviewAndConfirmConfiguration;
import com.mercadopago.android.px.internal.util.TextUtil;

public class SummaryProviderImpl implements SummaryProvider {

    private final Context context;
    private final ReviewAndConfirmConfiguration reviewAndConfirmConfiguration;

    public SummaryProviderImpl(Context context, ReviewAndConfirmConfiguration reviewAndConfirmConfiguration) {
        this.reviewAndConfirmConfiguration = reviewAndConfirmConfiguration;
        this.context = context;
    }

    @Override
    public String getSummaryProductsTitle() {
        String summaryProductTitle;

        if (!TextUtil.isEmpty(reviewAndConfirmConfiguration.getProductTitle())) {
            summaryProductTitle = reviewAndConfirmConfiguration.getProductTitle();
        } else {
            summaryProductTitle = context.getString(R.string.px_review_summary_product);
        }

        return summaryProductTitle;
    }

    @Override
    public int getDefaultTextColor() {
        return ContextCompat.getColor(context, R.color.px_summary_text_color);
    }

    @Override
    public String getSummaryShippingTitle() {
        return context.getString(R.string.px_review_summary_shipping);
    }

    @Override
    public int getDiscountTextColor() {
        return ContextCompat.getColor(context, R.color.px_discount_description);
    }

    @Override
    public String getSummaryArrearTitle() {
        return context.getString(R.string.px_review_summary_arrear);
    }

    @Override
    public String getSummaryTaxesTitle() {
        return context.getString(R.string.px_review_summary_taxes);
    }

    @Override
    public String getSummaryDiscountsTitle() {
        return context.getString(R.string.px_review_summary_discount);
    }

    @Override
    public int getDisclaimerTextColor() {
        int disclaimerTextColor;

        if (TextUtil.isEmpty(reviewAndConfirmConfiguration.getDisclaimerTextColor())) {
            disclaimerTextColor = ContextCompat.getColor(context, R.color.px_default_disclaimer);
        } else {
            disclaimerTextColor = Color.parseColor(reviewAndConfirmConfiguration.getDisclaimerTextColor());
        }

        return disclaimerTextColor;
    }

    @Override
    public String getSummaryChargesTitle() {
        return context.getString(R.string.px_review_summary_charges);
    }
}
