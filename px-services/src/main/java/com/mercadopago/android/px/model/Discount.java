package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.mercadopago.android.px.internal.util.CurrenciesUtil.isValidCurrency;

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

    protected Discount(final Builder builder) {
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

    private Discount(final Parcel in) {
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

    public static class Builder {
        //region mandatory params
        private final String id;
        private final String currencyId;
        private final BigDecimal couponAmount;
        //endregion mandatory params
        private String name;
        private BigDecimal percentOff;
        private BigDecimal amountOff;

        /**
         * Builder for discount construction
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

        @SuppressWarnings("unused")
        public Discount.Builder setName(@NonNull final String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("unused")
        public Discount.Builder setPercentOff(@NonNull final BigDecimal percentOff) {
            this.percentOff = percentOff;
            return this;
        }

        @SuppressWarnings("unused")
        public Discount.Builder setAmountOff(@NonNull final BigDecimal amountOff) {
            this.amountOff = amountOff;
            return this;
        }

        public Discount build() {
            if (!isValidCurrency(currencyId)) {
                throw new IllegalStateException("invalid currency id");
            }
            return new Discount(this);
        }
    }
}
