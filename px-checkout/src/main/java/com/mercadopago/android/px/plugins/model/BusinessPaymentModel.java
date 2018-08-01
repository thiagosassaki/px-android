package com.mercadopago.android.px.plugins.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.components.PaymentMethodComponent;
import com.mercadopago.android.px.components.TotalAmount;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import java.math.BigDecimal;

public class BusinessPaymentModel implements Parcelable {

    public final BusinessPayment payment;
    private final Discount discount;
    private final PaymentMethod paymentMethod;
    private final PayerCost payerCost;
    private final String currencyId;
    private final BigDecimal amount;

    @Nullable
    private final String lastFourDigits;

    public BusinessPaymentModel(final BusinessPayment payment,
        final Discount discount,
        final PaymentMethod paymentMethod,
        final PayerCost payerCost,
        final String currencyId,
        final BigDecimal amount,
        @Nullable final String lastFourDigits) {
        this.payment = payment;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.payerCost = payerCost;
        this.currencyId = currencyId;
        this.amount = amount;
        this.lastFourDigits = lastFourDigits;
    }

    public PaymentMethodComponent.PaymentMethodProps getPaymentMethodProps() {
        final TotalAmount.TotalAmountProps totalAmountProps = new TotalAmount.TotalAmountProps(currencyId, amount,
            payerCost,
            discount);
        return new PaymentMethodComponent.PaymentMethodProps(paymentMethod,
            lastFourDigits,
            payment.getStatementDescription(),
            totalAmountProps);
    }

    protected BusinessPaymentModel(final Parcel in) {
        payment = in.readParcelable(BusinessPayment.class.getClassLoader());
        discount = in.readParcelable(Discount.class.getClassLoader());
        paymentMethod = in.readParcelable(PaymentMethod.class.getClassLoader());
        payerCost = in.readParcelable(PayerCost.class.getClassLoader());
        currencyId = in.readString();
        amount = new BigDecimal(in.readString());
        lastFourDigits = in.readString();
    }

    public static final Creator<BusinessPaymentModel> CREATOR = new Creator<BusinessPaymentModel>() {
        @Override
        public BusinessPaymentModel createFromParcel(final Parcel in) {
            return new BusinessPaymentModel(in);
        }

        @Override
        public BusinessPaymentModel[] newArray(final int size) {
            return new BusinessPaymentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(payment, flags);
        dest.writeParcelable(discount, flags);
        dest.writeParcelable(paymentMethod, flags);
        dest.writeParcelable(payerCost, flags);
        dest.writeString(currencyId);
        dest.writeString(amount.toString());
        dest.writeString(lastFourDigits);
    }
}
