package com.mercadopago.android.px.internal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.PaymentType;
import com.mercadopago.android.px.model.PaymentTypes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */
public class PaymentTypesAdapter extends RecyclerView.Adapter<PaymentTypesAdapter.ViewHolder> {

    private final List<PaymentType> mPaymentTypes;
    private final OnSelectedCallback<Integer> mCallback;

    public PaymentTypesAdapter(OnSelectedCallback<Integer> callback) {
        mPaymentTypes = new ArrayList<>();
        mCallback = callback;
    }

    public void addResults(List<PaymentType> list) {
        mPaymentTypes.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mPaymentTypes.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adapterView = inflater.inflate(R.layout.px_adapter_payment_types, parent, false);
        return new ViewHolder(adapterView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentType paymentType = mPaymentTypes.get(position);
        holder.mPaymentTypeIdTextView.setText(paymentTypeName(paymentType, holder.mPaymentTypeIdTextView.getContext()));
    }

    public String paymentTypeName(final PaymentType paymentType,
        final Context context) {
        String ans = "";
        if (paymentType.getId().equals(PaymentTypes.CREDIT_CARD)) {
            ans = context.getString(R.string.px_credit_payment_type);
        } else if (paymentType.getId().equals(PaymentTypes.DEBIT_CARD)) {
            ans = context.getString(R.string.px_debit_payment_type);
        } else if (paymentType.getId().equals(PaymentTypes.PREPAID_CARD)) {
            ans = context.getString(R.string.px_form_card_title_payment_type_prepaid);
        }
        return ans;
    }

    @Override
    public int getItemCount() {
        return mPaymentTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /* default */ MPTextView mPaymentTypeIdTextView;

        /* default */ ViewHolder(View itemView) {
            super(itemView);
            mPaymentTypeIdTextView = itemView.findViewById(R.id.mpsdkPaymentTypeTextView);
            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
