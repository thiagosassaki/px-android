package com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.PaymentModel;
import com.mercadopago.android.px.internal.util.MercadoPagoUtil;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.view.CompactComponent;

class MethodOff extends CompactComponent<MethodOff.Props, Void> {

    static class Props {

        final String id;
        final String title;
        final Integer time;

        private Props(String id, String title, Integer time) {
            this.id = id;
            this.title = title;
            this.time = time;
        }

        static Props createFrom(final PaymentModel props) {
            return new Props(props.paymentMethodId,
                props.paymentMethodName,
                props.accreditationTime);
        }
    }

    MethodOff(final Props props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        View paymentView = inflate(parent, R.layout.px_payment_method_off);

        TextView time = paymentView.findViewById(R.id.time);
        time.setText(MercadoPagoUtil.getAccreditationTimeMessage(time.getContext(), props.time));

        TextView title = paymentView.findViewById(R.id.title);
        title.setText(props.title);

        ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        return paymentView;
    }
}
