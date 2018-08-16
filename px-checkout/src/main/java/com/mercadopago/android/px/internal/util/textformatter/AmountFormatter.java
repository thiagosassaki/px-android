package com.mercadopago.android.px.internal.util.textformatter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import com.mercadopago.android.px.model.Discount;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.mercadopago.android.px.internal.util.CurrenciesUtil.getLocalizedAmount;
import static com.mercadopago.android.px.internal.util.CurrenciesUtil.getLocalizedAmountNoDecimals;

public class AmountFormatter extends ChainFormatter {

    @NonNull final CurrencyFormatter currencyFormatter;
    @NonNull private BigDecimal total;

    AmountFormatter(@NonNull final BigDecimal baseAmount,
        @NonNull final CurrencyFormatter currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
        total = BigDecimal.ZERO;
        total = total.add(baseAmount);
    }

    @NonNull
    public AmountFormatter add(@NonNull final BigDecimal toAdd) {
        total = total.add(toAdd);
        return this;
    }

    @NonNull
    public AmountFormatter substract(@NonNull final BigDecimal toSubstract) {
        total = total.subtract(toSubstract);
        return this;
    }

    @NonNull
    public AmountFormatter add(@Nullable final Discount toAdd) {
        total = total.add(getAmount(toAdd));
        return this;
    }

    @NonNull
    public AmountFormatter substract(@Nullable final Discount toSubstract) {
        total = total.subtract(getAmount(toSubstract));
        return this;
    }

    @NonNull
    private BigDecimal getAmount(final @Nullable Discount discount) {
        return discount == null ? BigDecimal.ZERO : discount.getCouponAmount();
    }

    /**
     * Full display appears like 1,000.01
     *
     * @return style to shouldBeTriggered
     */
    @NonNull
    public Style normalDecimals() {
        return new StyleNormalDecimal(this);
    }

    /**
     * Small display appears like 1,000 ^ 01
     *
     * @return style to shouldBeTriggered
     */
    @NonNull
    public Style smallDecimals() {
        return new StyleSmallDecimal(this);
    }

    @Override
    protected Spannable apply(final CharSequence charSequence) {
        final BigDecimal remainder = total.remainder(BigDecimal.ONE);
        final String localized;
        if (remainder.compareTo(BigDecimal.ZERO) == 0) {
            // no decimals
            final BigDecimal truncated = total.setScale(0, RoundingMode.DOWN);
            localized = getLocalizedAmountNoDecimals(truncated, currencyFormatter.currency);
        } else {
            localized = getLocalizedAmount(total, currencyFormatter.currency);
        }
        return currencyFormatter.apply(localized);
    }
}
