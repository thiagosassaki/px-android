package com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.MPTextView;

public class PaymentMethodInfoController implements PaymentMethodSearchViewController {

    private static final int COMMENT_MAX_LENGTH = 75;

    protected PaymentMethodInfoModel item;
    protected Context context;
    protected View view;
    protected MPTextView name;
    protected MPTextView description;
    protected ImageView icon;
    protected View.OnClickListener listener;

    public PaymentMethodInfoController(@NonNull final Context context,
        @NonNull final PaymentMethodInfoModel item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
        view = LayoutInflater.from(context)
            .inflate(R.layout.px_row_pm_info_item, parent, attachToRoot);
        if (listener != null) {
            view.setOnClickListener(listener);
        }
        view.setTag(item.getId());
        return view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void initializeControls() {
        name = view.findViewById(R.id.px_name);
        description = view.findViewById(R.id.px_description);
        icon = view.findViewById(R.id.px_image);
    }

    @Override
    public void draw() {

        if (TextUtil.isEmpty(item.getName())) {
            name.setVisibility(View.GONE);
        } else {
            name.setVisibility(View.VISIBLE);
            name.setText(item.getName());
        }

        if (shouldShowDescription()) {
            description.setText(item.getDescription());
        }

        if (item.getIcon() == R.drawable.px_none) {
            icon.setVisibility(View.GONE);
        } else {
            icon.setVisibility(View.VISIBLE);
            icon.setImageResource(item.getIcon());
        }
    }

    @VisibleForTesting
    boolean shouldShowDescription() {
        return TextUtil.isNotEmpty(item.getDescription()) && item.getDescription().length() < COMMENT_MAX_LENGTH;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }
}
