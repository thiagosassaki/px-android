package com.mercadopago.android.px.internal.features.uicontrollers;

import android.content.Context;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethods.PaymentMethodOffEditableRow;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethods.PaymentMethodViewController;
import com.mercadopago.android.px.internal.features.uicontrollers.paymentmethods.card.PaymentMethodOnEditableRow;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.Token;

/**
 * Created by mreverter on 29/4/16.
 */
public class ViewControllerFactory {

    public static PaymentMethodViewController getPaymentMethodOnEditionViewController(Context context,
        PaymentMethod paymentMethod, Token token) {
        return new PaymentMethodOnEditableRow(context, paymentMethod, token);
    }

    public static PaymentMethodViewController getPaymentMethodOffEditionViewController(Context context,
        PaymentMethod paymentMethod) {
        return new PaymentMethodOffEditableRow(context, paymentMethod);
    }

    public static PaymentMethodViewController getPaymentMethodOffEditionViewController(Context context,
        PaymentMethodSearchItem item) {
        return new PaymentMethodOffEditableRow(context, item);
    }
}
