package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.mercadopago.android.px.internal.util.CurrenciesUtil.isValidCurrency;

/**
 * Model that represents the discount which will be applied to a payment.
 */
@SuppressWarnings("unused")
public class Discount implements Serializable, Parcelable {
    /**
     * Discount id is the campaign_id
     */
    private final String id;

    private final String name;
    private final String currencyId;
    private final BigDecimal percentOff;
    private final BigDecimal amountOff;
    private final BigDecimal couponAmount;

    /* default */ Discount(final Builder builder) {
        id = builder.id;
        currencyId = builder.currencyId;
        couponAmount = builder.couponAmount;
        name = builder.name;
        percentOff = builder.percentOff;
        amountOff = builder.amountOff;
    }

    public BigDecimal getAmountOff() {
        return amountOff;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPercentOff() {
        return percentOff;
    }

    public BigDecimal getAmountWithDiscount(final BigDecimal amount) {
        return amount.subtract(couponAmount);
    }

    public boolean hasPercentOff() {
        return percentOff != null && !BigDecimal.ZERO.equals(percentOff);
    }

    /* default */ Discount(final Parcel in) {
        id = in.readString();
        name = in.readString();
        currencyId = in.readString();
        percentOff = ParcelableUtil.getOptionalBigDecimal(in);
        amountOff = ParcelableUtil.getOptionalBigDecimal(in);
        couponAmount = new BigDecimal(in.readString());
    }

    public static final Creator<Discount> CREATOR = new Creator<Discount>() {
        @Override
        public Discount createFromParcel(final Parcel in) {
            return new Discount(in);
        }

        @Override
        public Discount[] newArray(final int size) {
            return new Discount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(currencyId);
        ParcelableUtil.writeOptional(dest, percentOff);
        ParcelableUtil.writeOptional(dest, amountOff);
        dest.writeString(couponAmount.toString());
    }

    @SuppressWarnings("unused")
    public static class Builder {

        //region mandatory params
        /* default */ @NonNull private final String id;
        /* default */ @NonNull private final String currencyId;
        /* default */ @NonNull private final BigDecimal couponAmount;
        //endregion mandatory params

        /* default */ @Nullable private String name;
        /* default */ @Nullable private BigDecimal percentOff;
        /* default */ @Nullable private BigDecimal amountOff;

        /**
         * Builder for discount construction.
         * This discount have to be created in Mercado Pago.
         *
         * @param id discount id
         * @param currencyId amount currency id
         * @param couponAmount amount that will be applied in discount
         */
        public Builder(@NonNull final String id,
            @NonNull final String currencyId,
            @NonNull final BigDecimal couponAmount) {
            this.id = id;
            this.currencyId = currencyId;
            this.couponAmount = couponAmount;
            setPercentOff(BigDecimal.ZERO);
            setAmountOff(BigDecimal.ZERO);
        }

        /**
         * Discount name that will be shown along the payment process.
         *
         * @param name discount name.
         * @return builder
         */
        public Discount.Builder setName(@NonNull final String name) {
            this.name = name;
            return this;
        }

        /**
         * This value represents the discount percent off which will be applied to the total amount.
         * Percent off is an optional value. By default, if percent off is null or zero, the value that
         * will be shown along the payment process will be coupon amount.
         *
         * @param percentOff discount percent off that will be applied.
         * @return builder
         */
        public Discount.Builder setPercentOff(@NonNull final BigDecimal percentOff) {
            this.percentOff = percentOff;
            return this;
        }

        /**
         * This value represents the discount amount off which will be applied to the total amount.
         * Amount off is an optional value. By default, if amount off is null or zero, the value that
         * will be shown along the payment process will be coupon amount.
         *
         * @param amountOff discount amount that will be applied.
         * @return builder
         */
        public Discount.Builder setAmountOff(@NonNull final BigDecimal amountOff) {
            this.amountOff = amountOff;
            return this;
        }

        /**
         * It creates the discount that will be applied.
         *
         * @return Discount
         */
        public Discount build() {
            if (!isValidCurrency(currencyId)) {
                throw new IllegalStateException("invalid currency id");
            }
            return new Discount(this);
        }
    }
}
