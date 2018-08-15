package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsRenderer extends Renderer<Instructions> {

    @Override
    public View render(final Instructions component, final Context context, final ViewGroup parent) {
        final View instructionsView = inflate(R.layout.px_payment_result_instructions, parent);
        final ViewGroup parentViewGroup = instructionsView.findViewById(R.id.mpsdkInstructionsContainer);

        if (component.hasSubtitle()) {
            RendererFactory.create(context, component.getSubtitleComponent()).render(parentViewGroup);
        }

        RendererFactory.create(context, component.getContentComponent()).render(parentViewGroup);

        //TODO backend refactor: secondary info should be an email related component
        if (component.hasSecondaryInfo() && component.shouldShowEmailInSecondaryInfo()) {
            RendererFactory.create(context, component.getSecondaryInfoComponent()).render(parentViewGroup);
        }

        return instructionsView;
    }
}
