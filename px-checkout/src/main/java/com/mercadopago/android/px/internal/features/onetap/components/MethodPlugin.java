package com.mercadopago.android.px.internal.features.onetap.components;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.PaymentMethodInfo;
import javax.annotation.Nonnull;

class MethodPlugin extends CompactComponent<MethodPlugin.Props, Void> {

    /* default */ static class Props {

        /* default */ @NonNull final String paymentMethodId;

        /* default */ Props(@NonNull final String paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        /* default */
        static Props createFrom(final PaymentMethod.Props props) {
            return new Props(props.paymentMethodId);
        }
    }

    /* default */ MethodPlugin(final Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final View main = inflate(parent, R.layout.px_payment_method_plugin_compact);
        final PluginRepository pluginService = Session.getSession(parent.getContext()).getPluginRepository();
        final PaymentMethodInfo pluginInfo = pluginService.getPaymentMethodInfo(props.paymentMethodId);
        final ImageView logo = main.findViewById(R.id.icon);
        final TextView name = main.findViewById(R.id.name);
        final TextView description = main.findViewById(R.id.description);
        logo.setImageResource(pluginInfo.icon);
        ViewUtils.loadOrGone(pluginInfo.getName(), name);
        ViewUtils.loadOrGone(pluginInfo.getDescription(), description);

        return main;
    }
}
