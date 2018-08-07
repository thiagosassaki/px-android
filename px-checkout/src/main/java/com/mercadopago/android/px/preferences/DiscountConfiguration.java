package com.mercadopago.android.px.preferences;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import java.io.Serializable;

@SuppressWarnings("unused")
public class DiscountConfiguration implements Serializable {

    @Nullable private Discount discount;
    @Nullable private Campaign campaign;

    /**
     * Set Mercado Pago discount that will be applied to total amount.
     * When you set a discount with its campaign, we do not check in discount service.
     * You have to set a payment processor for discount be applied.
     *
     * @param discount Mercado Pago discount.
     * @param campaign Discount campaign with discount data.
     */
    public DiscountConfiguration(@NonNull final Discount discount,
        @NonNull final Campaign campaign) {

        this.discount = discount;
        this.campaign = campaign;
    }

    //TODO error case - in progress development
    public DiscountConfiguration() {
    }

    @Nullable
    public Discount getDiscount() {
        return discount;
    }

    @Nullable
    public Campaign getCampaign() {
        return campaign;
    }
}
