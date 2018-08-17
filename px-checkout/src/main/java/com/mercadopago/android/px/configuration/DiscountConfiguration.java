package com.mercadopago.android.px.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import java.io.Serializable;

@SuppressWarnings("unused")
public final class DiscountConfiguration implements Serializable {

    @Nullable private final Discount discount;
    @Nullable private final Campaign campaign;
    private final boolean isNotAvailable;

    /**
     * Set Mercado Pago discount that will be applied to total amount.
     * When you set a discount with its campaign, we do not check in discount service.
     * You have to set a payment processor for discount be applied.
     *
     * @param discount Mercado Pago discount.
     * @param campaign Discount campaign with discount data.
     */
    public static DiscountConfiguration withDiscount(@NonNull final Discount discount,
        @NonNull final Campaign campaign) {
        return new DiscountConfiguration(discount, campaign);
    }

    /**
     * When you have the user have wasted all the discounts available
     * this kind of configuration will show a generic message to the user.
     *
     * @return discount configuration
     */
    public static DiscountConfiguration forNotAvailableDiscount() {
        return new DiscountConfiguration();
    }

    private DiscountConfiguration(@NonNull final Discount discount,
        @NonNull final Campaign campaign) {
        this.discount = discount;
        this.campaign = campaign;
        isNotAvailable = false;
    }

    private DiscountConfiguration() {
        discount = null;
        campaign = null;
        isNotAvailable = true;
    }

    @Nullable
    public Discount getDiscount() {
        return discount;
    }

    @Nullable
    public Campaign getCampaign() {
        return campaign;
    }

    public boolean isNotAvailable() {
        return isNotAvailable;
    }
}
