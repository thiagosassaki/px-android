package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;

/**
 * Created by vaserber on 10/20/17.
 */

public class PaymentResultRenderer extends Renderer<PaymentResultContainer> {

    @Override
    public View render(final PaymentResultContainer component, final Context context, final ViewGroup parent) {
        final View view;

        if (component.isLoading()) {

            view = RendererFactory.create(context, component.getLoadingComponent()).render(parent);
        } else {

            view = inflate(R.layout.px_payment_result_container, parent);
            final ViewGroup parentViewGroup = view.findViewById(R.id.mpsdkPaymentResultContainer);

            RendererFactory.create(context, component.getHeaderComponent()).render(parentViewGroup);

            if (component.hasBodyComponent()) {
                RendererFactory.create(context, component.getBodyComponent()).render(parentViewGroup);
            }

            View footer = RendererFactory.create(context, component.getFooterContainer()).render(parentViewGroup);
            parentViewGroup.addView(footer);
        }

        return view;
    }
}