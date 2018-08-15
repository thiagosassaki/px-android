package com.mercadopago.android.px.internal.features.hooks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.NextAction;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.view.ToolbarComponent;
import com.mercadopago.android.px.model.PaymentData;
import java.util.Map;

public abstract class HookComponent<T> extends Component<HookComponent.Props, T> {

    static {
        RendererFactory.register(HookComponent.class, HookRenderer.class);
    }

    public HookComponent(@NonNull final Props props) {
        super(props);
    }

    public static class Props {

        public final Map<String, Object> data;
        public final String paymentTypeId;
        public final PaymentData paymentData;
        public final String toolbarTitle;
        public final boolean toolbarVisible;

        public Props(@NonNull final Builder builder) {
            data = builder.data;
            paymentTypeId = builder.paymentTypeId;
            paymentData = builder.paymentData;
            toolbarTitle = builder.toolbarTitle;
            toolbarVisible = builder.toolbarVisible;
        }

        public Builder toBuilder() {
            return new Builder()
                .setData(data)
                .setPaymentTypeId(paymentTypeId)
                .setPaymentData(paymentData)
                .setToolbarTitle(toolbarTitle)
                .setToolbarVisible(toolbarVisible);
        }

        public static class Builder {
            public Map<String, Object> data;
            public String paymentTypeId;
            public PaymentData paymentData;
            public String toolbarTitle = "";
            public boolean toolbarVisible = true;

            public Builder setData(@NonNull final Map<String, Object> data) {
                this.data = data;
                return this;
            }

            public Builder setPaymentTypeId(@NonNull final String paymentTypeId) {
                this.paymentTypeId = paymentTypeId;
                return this;
            }

            public Builder setPaymentData(@NonNull final PaymentData paymentData) {
                this.paymentData = paymentData;
                return this;
            }

            public Builder setToolbarTitle(@NonNull final String toolbarTitle) {
                this.toolbarTitle = toolbarTitle;
                return this;
            }

            public Builder setToolbarVisible(@NonNull final boolean toolbarVisible) {
                this.toolbarVisible = toolbarVisible;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }

    public ToolbarComponent getToolbarComponent() {
        final ToolbarComponent.Props props = new ToolbarComponent.Props.Builder()
            .setToolbarTitle(this.props.toolbarTitle)
            .setToolbarVisible(this.props.toolbarVisible)
            .build();
        return new ToolbarComponent(props);
    }

    public void onContinue() {
        getDispatcher().dispatch(new NextAction());
    }
}