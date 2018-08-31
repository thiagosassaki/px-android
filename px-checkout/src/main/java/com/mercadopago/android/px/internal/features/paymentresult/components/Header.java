package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.HeaderProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.IconProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.RendererFactory;

public class Header extends Component<HeaderProps, Void> {

    static {
        RendererFactory.register(Header.class, HeaderRenderer.class);
    }

    public Header(@NonNull final HeaderProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public Icon getIconComponent() {

        final IconProps iconProps = new IconProps.Builder()
            .setIconImage(props.iconImage)
            .setIconUrl(props.iconUrl)
            .setBadgeImage(props.badgeImage)
            .build();

        return new Icon(iconProps, getDispatcher());
    }
}