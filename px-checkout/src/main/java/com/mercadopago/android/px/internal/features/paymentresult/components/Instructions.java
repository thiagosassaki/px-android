package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsContentProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsSecondaryInfoProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsSubtitleProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.constants.ProcessingModes;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class Instructions extends Component<InstructionsProps, Void> {

    public Instructions(@NonNull final InstructionsProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public InstructionsSubtitle getSubtitleComponent() {
        final InstructionsSubtitleProps subtitleProps = new InstructionsSubtitleProps.Builder()
            .setSubtitle(props.instruction.getSubtitle())
            .build();

        return new InstructionsSubtitle(subtitleProps, getDispatcher());
    }

    public InstructionsContent getContentComponent() {
        final InstructionsContentProps contentProps = new InstructionsContentProps.Builder()
            .setInstruction(props.instruction)
            .build();

        return new InstructionsContent(contentProps, getDispatcher());
    }

    public InstructionsSecondaryInfo getSecondaryInfoComponent() {
        final InstructionsSecondaryInfoProps secondaryInfoProps = new InstructionsSecondaryInfoProps.Builder()
            .setSecondaryInfo(props.instruction.getSecondaryInfo())
            .build();

        return new InstructionsSecondaryInfo(secondaryInfoProps, getDispatcher());
    }

    public boolean hasSubtitle() {
        final String subtitle = props.instruction.getSubtitle();
        return subtitle != null && !subtitle.isEmpty();
    }

    public boolean hasSecondaryInfo() {
        final List<String> secondaryInfoList = props.instruction.getSecondaryInfo();
        return secondaryInfoList != null && !secondaryInfoList.isEmpty();
    }

    public boolean shouldShowEmailInSecondaryInfo() {
        return props.processingMode.equals(ProcessingModes.AGGREGATOR);
    }
}
