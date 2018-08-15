package com.mercadopago.android.px.internal.features.providers;

import android.content.Context;
import com.mercadopago.android.px.R;

/**
 * Created by vaserber on 8/17/17.
 */

public class ReviewPaymentMethodsProviderImpl implements ReviewPaymentMethodsProvider {

    private final Context context;

    public ReviewPaymentMethodsProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public String getEmptyPaymentMethodsListError() {
        return context.getString(R.string.px_error_message_detail_no_payment_method_list);
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.px_standard_error_message);
    }
}
