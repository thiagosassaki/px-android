package com.mercadopago.android.px.internal.features.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Site;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mromar on 3/2/18.
 */

public class SummaryModel implements Parcelable {

    private final String amount;
    public final String currencyId;
    public final String siteId;
    private final String paymentTypeId;
    private final String payerCostTotalAmount;
    private final int installments;
    private final String cftPercent;
    private final String couponAmount;
    private final boolean hasPercentOff;
    private final String installmentsRate;
    private final String installmentAmount;
    public final String title;
    private final String itemsAmount;
    private final String charges;

    public SummaryModel(final BigDecimal amount,
        final PaymentMethod paymentMethod,
        final Site site,
        final PayerCost payerCost,
        final Discount discount,
        final String title,
        final BigDecimal itemsAmount,
        final BigDecimal charges) {

        this.amount = amount.toString();
        currencyId = site.getCurrencyId();
        siteId = site.getId();
        paymentTypeId = paymentMethod.getPaymentTypeId();
        payerCostTotalAmount =
            payerCost != null && payerCost.getTotalAmount() != null ? payerCost.getTotalAmount().toString() : null;
        installments = payerCost != null && payerCost.getInstallments() != null ? payerCost.getInstallments() : 1;
        cftPercent = payerCost != null && payerCost.getCFTPercent() != null ? payerCost.getCFTPercent() : null;
        couponAmount = discount != null ? discount.getCouponAmount().toString() : null;
        hasPercentOff = discount != null && discount.hasPercentOff();
        installmentsRate =
            payerCost != null && payerCost.getInstallmentRate() != null ? payerCost.getInstallmentRate().toString()
                : null;
        installmentAmount =
            payerCost != null && payerCost.getInstallmentAmount() != null ? payerCost.getInstallmentAmount().toString()
                : null;
        this.title = title;
        this.itemsAmount = itemsAmount.toString();
        this.charges = charges.toString();
    }

    protected SummaryModel(final Parcel in) {
        amount = in.readString();
        currencyId = in.readString();
        siteId = in.readString();
        paymentTypeId = in.readString();
        payerCostTotalAmount = in.readString();
        installments = in.readInt();
        cftPercent = in.readString();
        couponAmount = in.readString();
        hasPercentOff = in.readByte() != 0;
        installmentsRate = in.readString();
        installmentAmount = in.readString();
        title = in.readString();
        itemsAmount = in.readString();
        charges = in.readString();
    }

    public static final Creator<SummaryModel> CREATOR = new Creator<SummaryModel>() {
        @Override
        public SummaryModel createFromParcel(final Parcel in) {
            return new SummaryModel(in);
        }

        @Override
        public SummaryModel[] newArray(final int size) {
            return new SummaryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(amount);
        dest.writeString(currencyId);
        dest.writeString(siteId);
        dest.writeString(paymentTypeId);
        dest.writeString(payerCostTotalAmount);
        dest.writeInt(installments);
        dest.writeString(cftPercent);
        dest.writeString(couponAmount);
        dest.writeByte((byte) (hasPercentOff ? 1 : 0));
        dest.writeString(installmentsRate);
        dest.writeString(installmentAmount);
        dest.writeString(title);
        dest.writeString(itemsAmount);
        dest.writeString(charges);
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public String getCftPercent() {
        return cftPercent;
    }

    public BigDecimal getAmountToPay() {
        return new BigDecimal(amount);
    }

    public BigDecimal getItemsAmount() {
        return new BigDecimal(itemsAmount);
    }

    @Nullable
    public BigDecimal getPayerCostTotalAmount() {
        return payerCostTotalAmount != null ? new BigDecimal(payerCostTotalAmount) : null;
    }

    @Nullable
    public BigDecimal getCouponAmount() {
        return couponAmount != null ? new BigDecimal(couponAmount) : null;
    }

    @Nullable
    public BigDecimal getInstallmentsRate() {
        return installmentsRate != null ? new BigDecimal(installmentsRate) : null;
    }

    @Nullable
    public BigDecimal getInstallmentAmount() {
        return installmentAmount != null ? new BigDecimal(installmentAmount) : null;
    }

    @NonNull
    public BigDecimal getCharges() {
        return new BigDecimal(charges);
    }

    public int getInstallments() {
        return installments;
    }

    public boolean hasMultipleInstallments() {
        return getInstallments() > 1;
    }

    public boolean hasCoupon() {
        return getCouponAmount() != null;
    }

    public static String resolveTitle(final List<Item> items, final String singularTitle, final String pluralTitle) {
        final String title;

        if (items.size() == 1) {
            if (TextUtil.isEmpty(items.get(0).getTitle())) {
                if (items.get(0).getQuantity() > 1) {
                    title = pluralTitle;
                } else {
                    title = singularTitle;
                }
            } else {
                title = items.get(0).getTitle();
            }
        } else {
            title = pluralTitle;
        }

        return title;
    }

    public boolean hasCharges() {
        return BigDecimal.ZERO.compareTo(getCharges()) != 0;
    }
}

