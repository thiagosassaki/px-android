package com.mercadopago.android.px.internal.features.explode;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.paymentresult.PaymentResultDecorator;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.IPayment;

public class ExplodeDecoratorMapper extends Mapper<IPayment, ExplodeDecorator> {

    @Override
    public ExplodeDecorator map(@NonNull final IPayment val) {
        if (PaymentResultDecorator.isSuccessBackground(val)) {

            int image = 0;
            if (PaymentResultDecorator.isCheckBagde(val)) {
                image = R.drawable.px_ic_payment_success;
            } else if (PaymentResultDecorator.isPendingSuccessBadge(val)) {
                image = R.drawable.px_ic_payment_pending;
            }
            return new ExplodeDecorator(R.color.ui_components_success_color, R.color.px_green_status_bar,
                image);

        } else if (PaymentResultDecorator.isPendingOrErrorRecoverableBackground(val)) {

            int image = 0;
            if (PaymentResultDecorator.isPendingWarningBadge(val)) {
                image = R.drawable.px_ic_payment_pending;
            } else if (PaymentResultDecorator.isErrorRecoverableBadge(val)) {
                image = R.drawable.px_ic_payment_warning;
            }
            return new ExplodeDecorator(R.color.ui_components_warning_color, R.color.px_orange_status_bar,
                image);

        } else if (PaymentResultDecorator.isErrorNonRecoverableBackground(val)) {
            return new ExplodeDecorator(R.color.ui_components_error_color, R.color.px_red_status_bar,
                R.drawable.px_ic_payment_error);
        } else {
            return new ExplodeDecorator(R.color.px_colorPrimary, R.color.px_blue_status_bar,
                R.drawable.px_ic_payment_pending);
        }
    }
}
