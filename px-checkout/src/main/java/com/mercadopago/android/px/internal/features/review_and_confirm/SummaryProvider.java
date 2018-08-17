package com.mercadopago.android.px.internal.features.review_and_confirm;

import com.mercadopago.android.px.internal.base.ResourcesProvider;

/**
 * Created by mromar on 3/5/18.
 */

public interface SummaryProvider extends ResourcesProvider {

    String getSummaryProductsTitle();

    int getDefaultTextColor();

    String getSummaryShippingTitle();

    int getDiscountTextColor();

    String getSummaryArrearTitle();

    String getSummaryTaxesTitle();

    String getSummaryDiscountsTitle();

    int getDisclaimerTextColor();

    String getSummaryChargesTitle();
}
