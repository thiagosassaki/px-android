package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;

/**
 * Created by vaserber on 27/11/2017.
 */

public class BodyErrorRenderer extends Renderer<BodyError> {

    @Override
    public View render(final BodyError component, final Context context, final ViewGroup parent) {
        final View bodyErrorView = inflate(R.layout.px_payment_result_body_error, parent);
        final ViewGroup bodyViewGroup = bodyErrorView.findViewById(R.id.bodyErrorContainer);
        final MPTextView titleTextView = bodyErrorView.findViewById(R.id.paymentResultBodyErrorTitle);
        final MPTextView descriptionTextView = bodyErrorView.findViewById(R.id.paymentResultBodyErrorDescription);
        final MPTextView secondDescriptionTextView =
            bodyErrorView.findViewById(R.id.paymentResultBodyErrorSecondDescription);
        final AppCompatButton actionButton = bodyErrorView.findViewById(R.id.paymentResultBodyErrorAction);
        final View middleDivider = bodyErrorView.findViewById(R.id.bodyErrorMiddleDivider);
        final MPTextView secondaryTitleTextView = bodyErrorView.findViewById(R.id.bodyErrorSecondaryTitle);
        final View bottomDivider = bodyErrorView.findViewById(R.id.bodyErrorBottomDivider);

        setText(titleTextView, component.getTitle());
        setText(descriptionTextView, component.getDescription());
        setText(secondDescriptionTextView, component.getSecondDescription());

        if (component.getTitle().isEmpty()) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginTop = (int) context.getResources().getDimension(R.dimen.px_l_margin);
            params.setMargins(0, marginTop, 0, 0);
            descriptionTextView.setLayoutParams(params);
        }

        if (component.hasActionForCallForAuth()) {
            actionButton.setText(component.getActionText());
            actionButton.setVisibility(View.VISIBLE);
            middleDivider.setVisibility(View.VISIBLE);
            secondaryTitleTextView.setText(component.getSecondaryTitleForCallForAuth());
            secondaryTitleTextView.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    component.recoverPayment();
                }
            });
        } else {
            actionButton.setVisibility(View.GONE);
            middleDivider.setVisibility(View.GONE);
            secondaryTitleTextView.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        stretchHeight(bodyViewGroup);
        return bodyErrorView;
    }
}
