package com.mercadopago.android.px.internal.features.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.example.R;

public class SamplePaymentMethodPluginFragment extends Fragment {

    private View progressbar;
    private TextView errorLabel;
    private View continueButton;
    private EditText passwordView;
    private SamplePaymentMethodPresenter presenter;
    private SampleResourcesProvider sampleResourcesProvider;
    private PaymentMethodPlugin.OnPaymentMethodListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_second_factor_auth, container, false);

        passwordView = view.findViewById(R.id.password);
        continueButton = view.findViewById(R.id.button_continue);
        errorLabel = view.findViewById(R.id.error_label);
        progressbar = view.findViewById(R.id.progressbar);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText editText = view.findViewById(R.id.password);
                presenter.authenticate(editText.getText().toString());
            }
        });

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                    final EditText editText = view.findViewById(R.id.password);
                    presenter.authenticate(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.init(this);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof PaymentMethodPlugin.OnPaymentMethodListener) {
            listener = (PaymentMethodPlugin.OnPaymentMethodListener) context;
        }
        sampleResourcesProvider = new SampleResourcesProvider(context);
        presenter = new SamplePaymentMethodPresenter(sampleResourcesProvider);
    }

    @Override
    public void onDetach() {
        sampleResourcesProvider = null;
        presenter = null;
        super.onDetach();
    }

    public void update(final SamplePaymentMethodPresenter.SampleState state) {
        continueButton.setEnabled(!state.authenticating);
        passwordView.setEnabled(!state.authenticating);
        passwordView.setText(presenter.state.password);
        errorLabel.setVisibility(TextUtil.isEmpty(presenter.state.errorMessage)
            ? View.GONE : View.VISIBLE);
        errorLabel.setText(presenter.state.errorMessage);
        progressbar.setVisibility(presenter.state.authenticating ? View.VISIBLE : View.GONE);
        continueButton.setVisibility(presenter.state.authenticating ? View.GONE : View.VISIBLE);
    }

    public void next() {
        if (listener != null) {
            listener.next();
        }
    }
}
