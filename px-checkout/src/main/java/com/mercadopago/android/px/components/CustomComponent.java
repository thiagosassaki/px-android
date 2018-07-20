package com.mercadopago.android.px.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.Map;

public class CustomComponent extends Component<CustomComponent.Props, Void> {

    public CustomComponent(@NonNull final Props props) {
        super(props);
    }

    public static class Props {

        public final Map<String, Object> data;
        public final CheckoutPreference checkoutPreference;

        // TODO remove custom component with custom values
        public Props(@NonNull final Map<String, Object> data,
            @NonNull final CheckoutPreference checkoutPreference) {
            this.data = data;
            this.checkoutPreference = checkoutPreference;
        }
    }
}
