package com.mercadopago.android.px.internal.util.textformatter;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;
import java.util.Locale;

import static com.mercadopago.android.px.internal.util.CurrenciesUtil.getCurrency;

public class CurrencyFormatter extends ChainFormatter {

    final Currency currency;
    private boolean hasSpace;
    private boolean hasSymbol;

    CurrencyFormatter(final String currencyId) {
        currency = getCurrency(currencyId);
        hasSymbol = true;
    }

    public AmountFormatter amount(@NonNull BigDecimal amount) {
        return new AmountFormatter(amount, this);
    }

    @Override
    protected Spannable apply(final CharSequence charSequence) {
        final String space = hasSpace ? " " : "";
        final String symbol = hasSymbol ? currency.getSymbol() : "";
        return new SpannableString(String.format(Locale.getDefault(), "%s%s%s", symbol, space, charSequence));
    }

    public CurrencyFormatter noSpace() {
        hasSpace = false;
        return this;
    }

    public CurrencyFormatter withSpace() {
        hasSpace = true;
        return this;
    }

    public CurrencyFormatter noSymbol() {
        this.hasSymbol = false;
        return this;
    }
}
