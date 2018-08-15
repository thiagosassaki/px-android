package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.IconProps;
import com.mercadopago.android.px.internal.util.TextUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.RendererFactory;

/**
 * Created by vaserber on 10/23/17.
 */

public class Icon extends Component<IconProps, Void> {

    static {
        RendererFactory.register(Icon.class, IconRenderer.class);
    }

    public Icon(@NonNull final IconProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasIconFromUrl() {
        return !TextUtils.isEmpty(props.iconUrl);
    }
}
