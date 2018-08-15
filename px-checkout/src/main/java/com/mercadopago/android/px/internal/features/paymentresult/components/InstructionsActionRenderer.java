package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadopago.android.px.internal.view.LinkAction;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.model.InstructionAction;

public class InstructionsActionRenderer extends Renderer<InstructionsAction> {

    @Override
    public View render(@NonNull final InstructionsAction component, @NonNull final Context context,
        final ViewGroup parent) {

        final MeliButton button = new MeliButton(context);
        button.setType(MeliButton.Type.OPTION_PRIMARY);

        if (component.props.instructionAction.getTag().equals(InstructionAction.Tags.LINK)) {
            button.setText(component.props.instructionAction.getLabel());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    component.getDispatcher().dispatch(new LinkAction(component.props.instructionAction.getUrl()));
                }
            });
        }

        parent.addView(button);

        return parent;
    }
}
