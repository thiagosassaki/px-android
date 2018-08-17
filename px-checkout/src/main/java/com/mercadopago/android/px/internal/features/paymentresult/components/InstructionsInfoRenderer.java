package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsInfoRenderer extends Renderer<InstructionsInfo> {

    @Override
    public View render(final InstructionsInfo component, final Context context, final ViewGroup parent) {
        final View infoView = inflate(R.layout.px_payment_result_instructions_info, parent);
        final MPTextView infoTitle = infoView.findViewById(R.id.mpsdkInstructionsInfoTitle);
        final MPTextView infoContent = infoView.findViewById(R.id.mpsdkInstructionsInfoContent);
        final View bottomDivider = infoView.findViewById(R.id.mpsdkInstructionsInfoDividerBottom);

        setText(infoTitle, component.props.infoTitle);

        if (component.props.infoContent == null || component.props.infoContent.isEmpty()) {
            infoContent.setVisibility(View.GONE);
        } else {
            infoContent.setText(getInfoText(component.props.infoContent));
            infoContent.setVisibility(View.VISIBLE);
        }

        if (component.props.bottomDivider) {
            bottomDivider.setVisibility(View.VISIBLE);
        } else {
            bottomDivider.setVisibility(View.GONE);
        }

        return infoView;
    }

    private String getInfoText(final List<String> info) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < info.size(); i++) {
            stringBuilder.append(info.get(i));
            if (i != info.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
