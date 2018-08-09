package com.mercadopago.android.px.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.mercadolibre.android.ui.widgets.MeliDialog;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.components.DiscountDetailContainer;
import com.mercadopago.android.px.components.DiscountDetailContainer.Props.DialogTitleType;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.DiscountRepository;

public class DiscountDetailDialog extends MeliDialog {

    private static final String TAG = DiscountDetailDialog.class.getName();

    public static void showDialog(final FragmentManager supportFragmentManager) {
        DiscountDetailDialog discountDetailDialog = new DiscountDetailDialog();
        discountDetailDialog.show(supportFragmentManager, TAG);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DiscountRepository discountRepository = Session.getSession(view.getContext()).getDiscountRepository();

        if (discountRepository != null) {
            final ViewGroup container = view.findViewById(R.id.main_container);

            final DiscountDetailContainer discountDetailContainer = new DiscountDetailContainer(
                new DiscountDetailContainer.Props(DialogTitleType.BIG, discountRepository));
            discountDetailContainer.render(container);
        } else {
            dismiss();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.px_dialog_detail_discount;
    }

    @Nullable
    @Override
    public String getSecondaryExitString() {
        return getString(R.string.px_terms_and_conditions);
    }
}
