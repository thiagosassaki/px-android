package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableString;

class StyleNormalDecimal extends Style {

    StyleNormalDecimal(@NonNull final AmountFormatter amountFormatter) {
        super(amountFormatter);
    }

    @Override
    public Spannable apply(@StringRes final int holder, final Context context) {
        return new SpannableString(context.getString(holder, apply(null)));
    }

    @Override
    protected Spannable apply(final CharSequence charSequence) {
        return amountFormatter.apply(charSequence);
    }
}
