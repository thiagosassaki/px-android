package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;

/**
 * Created by vaserber on 11/13/17.
 */

public class AccreditationComment extends Component<AccreditationComment.Props, Void> {

    public AccreditationComment(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Props {

        public final String comment;

        public Props(@NonNull final String comment) {
            this.comment = comment;
        }

        public Props(@NonNull final Builder builder) {
            comment = builder.comment;
        }

        public Builder toBuilder() {
            return new Props.Builder()
                .setComment(comment);
        }

        public static final class Builder {
            public String comment;

            public Builder setComment(String comment) {
                this.comment = comment;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}
