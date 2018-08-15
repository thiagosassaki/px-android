package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionsRenderer extends Renderer<InstructionsActions> {

    @Override
    public View render(final InstructionsActions component, final Context context, final ViewGroup parent) {
        final View actionsView = inflate(R.layout.px_payment_result_instructions_actions, parent);
        final ViewGroup parentViewGroup = actionsView.findViewById(R.id.mpsdkInstructionsActionsContainer);

        List<InstructionsAction> actionComponentList = component.getActionComponents();
        for (InstructionsAction instructionsAction : actionComponentList) {
            RendererFactory.create(context, instructionsAction).render(parentViewGroup);
        }

        return actionsView;
    }
}
