package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;

public class TotalAmountRenderer extends Renderer<TotalAmount> {

    @Override
    public View render(@NonNull final TotalAmount component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.px_total_amount_component, parent);
        final MPTextView amountTitleTextView = bodyView.findViewById(R.id.mpsdkAmountTitle);
        final MPTextView amountDetailTextView = bodyView.findViewById(R.id.mpsdkAmountDetail);
        final MPTextView totalAmountTextView = bodyView.findViewById(R.id.mpsdkTotalAmount);

        setText(amountTitleTextView, component.getAmountTitle());
        setText(amountDetailTextView, component.getAmountDetail());

        totalAmountTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        if (component.props.discount != null) {
            if (component.hasPayerCostWithMultipleInstallments()) {
                TextFormatter.withCurrencyId(component.props.currencyId)
                    .withSpace()
                    .amount(component.props.payerCost.getTotalAmount())
                    .add(component.props.discount.getCouponAmount())
                    .normalDecimals()
                    .into(totalAmountTextView);
            } else {
                TextFormatter.withCurrencyId(component.props.currencyId)
                    .withSpace()
                    .amount(component.props.amount)
                    .add(component.props.discount.getCouponAmount())
                    .normalDecimals()
                    .into(totalAmountTextView);
            }
        } else {
            totalAmountTextView.setVisibility(View.GONE);
        }

        return bodyView;
    }
}
